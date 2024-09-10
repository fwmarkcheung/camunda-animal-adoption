package io.camunda.demo.pick_animal.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import io.camunda.demo.pick_animal.db.UserchoiceNotFoundException;
import io.camunda.demo.pick_animal.db.UserchoiceRepository;
import io.camunda.demo.pick_animal.model.Userchoice;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
// @SpringBootTest
public class PickAnimalServiceTest {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ssZ");

    private final String CAT_BASE_URI = "https://api.thecatapi.com/v1/images/search";
    private final String CAT_IMAGE_SRC = "https://cdn2.thecatapi.com/images/J-vpF8e1N.jpg";

    // @InjectMocks
    // @Autowired
    PickAnimalService service;

    @Mock
    UserchoiceRepository userChoiceRepository;

    @BeforeEach
    void setup() {

        service = new PickAnimalService(userChoiceRepository, CAT_BASE_URI);

    }

    private Userchoice createUserchoice(String username, String animal, String imageSrcURL) {
        StringBuffer userChoiceIdStringBuffer = new StringBuffer(username);
        userChoiceIdStringBuffer.append('-');

        Date now = Calendar.getInstance().getTime();
        userChoiceIdStringBuffer.append(simpleDateFormat.format(now));

        // Extract the file extension (e.g., jpg, png, etc.)
        String formatName = imageSrcURL.substring(imageSrcURL.lastIndexOf('.') + 1);

        return new Userchoice(userChoiceIdStringBuffer.toString(), username, now, animal, imageSrcURL, formatName, "");

    }

    // private String generateId(String username) {
    // StringBuffer userChoiceIdStringBuffer = new StringBuffer(username);
    // userChoiceIdStringBuffer.append('-');

    // Date now = Calendar.getInstance().getTime();
    // userChoiceIdStringBuffer.append(simpleDateFormat.format(now));

    // return userChoiceIdStringBuffer.toString();

    // }

    @Test
    public void testFindUserchoiceById_with_valid_Id() {

        // Given
        Userchoice userchoice = createUserchoice("Mark", "Cat", CAT_IMAGE_SRC);

        // When
        Mockito.when(userChoiceRepository.findUserchoiceById(userchoice.id())).thenReturn(Optional.of(userchoice));

        // Then
        Optional<Userchoice> record = service.findUserchoiceById(userchoice.id());
        Assertions.assertTrue(record.isPresent());
    }

    @Test
    public void testFindUserchoiceById_with_InValid_Id() {

        // Given
        Userchoice userchoice = createUserchoice("Eden", "Cat", CAT_IMAGE_SRC);

        // When
        Mockito.when(userChoiceRepository.findUserchoiceById(userchoice.id())).thenReturn(Optional.empty());

        // Then
        UserchoiceNotFoundException thrown = Assertions.assertThrows(UserchoiceNotFoundException.class, () -> {
            service.findUserchoiceById(userchoice.id());
        });
        String customErrorMsg = "User Choice with id:" + userchoice.id() + " not found!";
        Assertions.assertEquals(customErrorMsg, thrown.getMessage());
    }

    // @Test
    // public void testPickAnAnimalHappyPath() {

    // // Given
    // String username = "Mark";
    // String animal = "Bear";
    // // Userchoice userchoice = createUserchoice(username, animal, CAT_IMAGE_SRC);

    // // When
    // //
    // Mockito.when(userChoiceRepository.findById(userchoice.id())).thenReturn(Optional.of(userchoice));

    // // Then
    // Map<String, String> actualReturnData = service.pickAnAnimal(username,
    // animal);
    // Map<String, String> exceptedReturnData = Map.of("userChoiceRecordId",
    // userchoice.id());
    // Assertions.assertEquals(exceptedReturnData, actualReturnData);

    // }
    // userChoiceDBService.save(userchoice);

    // record = userChoiceDBService.findById(userchoice.id());
    // Assertions.assertEquals(true, record.isPresent());

    // }

}
