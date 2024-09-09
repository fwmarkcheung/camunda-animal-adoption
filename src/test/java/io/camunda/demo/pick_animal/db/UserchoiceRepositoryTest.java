package io.camunda.demo.pick_animal.db;

import io.camunda.demo.pick_animal.model.Userchoice;
import io.harperdb.core.Template;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserchoiceRepositoryTest {

    @Mock
    private Template template;

    @InjectMocks
    private UserchoiceRepository userchoiceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserchoiceById_Success() {
        // Arrange
        String id = "123";
        Userchoice userchoice = new Userchoice(id, "John", new Date(), "Cat", "jpg", "image_url", "base64Image");
        when(template.findById(Userchoice.class, id)).thenReturn(Optional.of(userchoice));

        // Act
        Optional<Userchoice> result = userchoiceRepository.findUserchoiceById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userchoice, result.get());
    }

    @Test
    void testFindUserchoiceById_NotFound() {
        // Arrange
        String id = "123";
        when(template.findById(Userchoice.class, id)).thenReturn(Optional.empty());

        // Act
        Optional<Userchoice> result = userchoiceRepository.findUserchoiceById(id);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testSave_Success() {
        // Arrange
        String id = "123";
        String userName = "John";
        Date creationDate = new Date();
        String animal = "Cat";
        String imageUrl = "http://example.com/image.jpg";
        String imageBase64String = "base64Image";

        Userchoice userchoice = new Userchoice(id, userName, creationDate, animal, "jpg", imageUrl, imageBase64String);

        when(template.insert(any(Userchoice.class))).thenReturn(true);

        // Act
        boolean result = userchoiceRepository.save(id, userName, creationDate, animal, imageUrl, imageBase64String);

        // Assert
        assertTrue(result);
        verify(template).insert(any(Userchoice.class));
    }

    @Test
    void testSave_Failure() {
        // Arrange
        String id = "123";
        String userName = "John";
        Date creationDate = new Date();
        String animal = "Cat";
        String imageUrl = "http://example.com/image.jpg";
        String imageBase64String = "base64Image";

        when(template.insert(any(Userchoice.class))).thenReturn(false);

        // Act
        boolean result = userchoiceRepository.save(id, userName, creationDate, animal, imageUrl, imageBase64String);

        // Assert
        assertFalse(result);
        verify(template).insert(any(Userchoice.class));
    }

    @Test
    void testGetBase64ImageStrFromHarperDB_Success() {
        // Arrange
        String recordId = "123";
        Userchoice userchoice = new Userchoice(recordId, "John", new Date(), "Cat", "jpg", "image_url", "base64Image");
        when(template.findById(Userchoice.class, recordId)).thenReturn(Optional.of(userchoice));

        // Act
        Pair<String, String> result = userchoiceRepository.getBase64ImageStrFromHarperDB(recordId);

        // Assert
        assertNotNull(result);

        // Random image might have a different extension than the sample jpg
        assertNotNull(result.getValue0(), "No image extension is provided");
        assertEquals("base64Image", result.getValue1());
    }

    @Test
    void testGetBase64ImageStrFromHarperDB_UserNotFound() {
        // Arrange
        String recordId = "123";
        when(template.findById(Userchoice.class, recordId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserchoiceNotFoundException.class, () -> {
            userchoiceRepository.getBase64ImageStrFromHarperDB(recordId);
        });
    }
}
