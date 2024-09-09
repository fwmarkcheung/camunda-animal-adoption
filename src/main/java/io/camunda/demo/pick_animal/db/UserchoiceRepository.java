package io.camunda.demo.pick_animal.db;

import java.util.Optional;

import io.camunda.demo.pick_animal.model.Userchoice;
import io.harperdb.core.Template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

import org.javatuples.Pair;

@Service
public class UserchoiceRepository {

    private final Template template;
    private final static Logger LOG = LoggerFactory.getLogger(UserchoiceRepository.class);

    public UserchoiceRepository(final Template template) {
        this.template = template;
    }

    /*
     * Find a userchoice by Id
     * 
     */
    public Optional<Userchoice> findUserchoiceById(final String id) {
        return template.findById(Userchoice.class, id);
    }

    /*
     * Save the user choice to the DB
     */

    public boolean save(String id, String userName, Date creationDate, String animal, String imageUrl,
            String imageBase64String) {
        // Extract the file extension (e.g., jpg, png, etc.)
        String formatName = imageUrl.substring(imageUrl.lastIndexOf('.') + 1);
        Userchoice userChoice = new Userchoice(id, userName, creationDate, animal, formatName, imageUrl,
                imageBase64String);
        final boolean isSuccess = template.insert(userChoice);
        LOG.debug("Saving user choice:{} to DB", userChoice);
        if (isSuccess == true) {
            LOG.info("User record saved with id: {}", userChoice.id());
        } else {
            final String msg = "";
            LOG.error("Failed to insert user choice: {} to db", userChoice);
        }
        return isSuccess;
    }

    /*
     * Get the base 64 image by id
     */
    public Pair<String, String> getBase64ImageStrFromHarperDB(String recordId) {

        Optional<Userchoice> userchoice = findUserchoiceById(recordId);
        Pair<String, String> pair;

        try {
            Userchoice record = userchoice.get();
            pair = new Pair<String, String>(record.imageType(),
                    record.imageBase64String());
            return pair;

        } catch (NoSuchElementException e) {
            throw new UserchoiceNotFoundException(recordId);
        }
    }
}