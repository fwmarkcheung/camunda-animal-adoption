package io.camunda.demo.pick_animal.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;

import io.camunda.demo.pick_animal.db.UserchoiceRepository;
import io.camunda.demo.pick_animal.model.Userchoice;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import jakarta.annotation.PostConstruct;
import org.javatuples.Pair;

@Service
public class PickAnimalService {

  private final static Logger LOG = LoggerFactory.getLogger(PickAnimalService.class);

  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ssZ");

  private final RestClient restClient = RestClient.create();

  @Value("${REMOTE_CAT_BASE_URI:https://api.thecatapi.com/v1/images/search}")
  private String catBaseURI;

  @Value("${REMOTE_DOG_BASE_URI:https://api.thedogapi.com/v1/images/search}")
  private String dogBaseURI;

  @Value("${REMOTE_BEAR_BASE_URI:https://placebear.com/cache/}")
  private String bearBaseURI;

  // Extract a list of bear images from the application.properties
  @Value("${REMOTE_BEAR_IMAGES:145-300.jpg,150-145.jpg,70-146.jpg,70-147.jpg,395-205.jpg,150-200.jpg,145-201.jpg}")
  private String bearImagesList;

  private String[] bearImagesArray;

  @Autowired
  private UserchoiceRepository userChoiceRepository;

  @Autowired
  private ImageService imageDownloadService;

  public PickAnimalService(UserchoiceRepository userChoiceDBService, String catBaseURI) {
    this.userChoiceRepository = userChoiceDBService;
    this.catBaseURI = catBaseURI;
  }

  public PickAnimalService() {

  }

  @PostConstruct
  private void postConstruct() {
    bearImagesArray = bearImagesList.split(",");
  }

  /*
   * User selects an animal, the service will randomly animal photo,
   * and save the choice to DB.
   */
  @JobWorker(type = "pick-an-animal")
  public Map<String, String> pickAnAnimal(@Variable(name = "userName") final String userName,
      @Variable(name = "animal") final String animal) {
    LOG.info("{} picked a {}", userName, animal);

    String baseURI = null;
    if (animal.equalsIgnoreCase("cat")) {
      baseURI = catBaseURI;

    } else if (animal.equalsIgnoreCase("dog")) {
      baseURI = dogBaseURI;

    } else if (animal.equalsIgnoreCase("bear")) {
      baseURI = bearBaseURI;

    } else {
      final String errorMsg = "Unsupported animal: " + animal;
      LOG.error(errorMsg);
      throw new RuntimeException(errorMsg);
    }

    Date now = Calendar.getInstance().getTime();
    String imageSrc = null;
    try {

      // Extract the random image url from the API call

      if (animal.equalsIgnoreCase("cat") || animal.equalsIgnoreCase("dog")) {
        String imageJsonResponseStr = restClient.get().uri(baseURI).accept(APPLICATION_JSON).retrieve()
            .onStatus(status -> status.value() != 200, (request, response) -> {
              throw new RuntimeException("Failed to get picture.  Error: " + response.getStatusText() + "statusCode="
                  + response.getStatusCode());
            }).body(String.class);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<ImageJsonResponse>>() {
        }.getType();

        List<ImageJsonResponse> imageJsonResponseList = gson.fromJson(imageJsonResponseStr, listType);

        imageSrc = imageJsonResponseList.get(0).url();

      } else {
        // Get a random bear pictures from the list
        Random random = new Random();
        String randomBearImage = bearImagesArray[random.nextInt(bearImagesArray.length)];

        StringBuilder bearimageSrc = new StringBuilder(bearBaseURI);
        bearimageSrc.append(randomBearImage);
        imageSrc = bearimageSrc.toString();

      }

      // Download the image from the URL
      Pair<String, String> imageData = imageDownloadService.downloadImageAndConvertToBase64(imageSrc);
      LOG.info("{} selected a {} from {}", userName, animal, imageSrc);

      // Append the current time to the user name to create the record id
      StringBuffer userChoiceIdStringBuffer = new StringBuffer(userName);
      userChoiceIdStringBuffer.append('-');

      userChoiceIdStringBuffer.append(simpleDateFormat.format(now));
      userChoiceIdStringBuffer.append('-');
      userChoiceIdStringBuffer.append(animal);

      String userChoiceId = userChoiceIdStringBuffer.toString();

      LOG.debug("Saving the choice to database.");
      userChoiceRepository.save(userChoiceId, userName, now, animal, imageSrc, imageData.getValue1());
      return Map.of("userChoiceRecordId", userChoiceId);
    } catch (Exception e) {
      final String errorMsg = "Failed to retrieve picture from the webservice: " + baseURI;
      LOG.error(errorMsg);
      throw new RuntimeException(errorMsg, e);
    }

  }

  public Optional<Userchoice> findUserchoiceById(String id) {
    LOG.info("Fetching user choice with id: {} from the DB.", id);
    return Optional
        .ofNullable(userChoiceRepository.findUserchoiceById(id).orElseThrow(() -> new UserchoiceNotFoundException(id)));
  }

}