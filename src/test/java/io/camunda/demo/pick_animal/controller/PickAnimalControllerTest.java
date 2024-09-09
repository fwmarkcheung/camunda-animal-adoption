package io.camunda.demo.pick_animal.controller;

import io.camunda.demo.pick_animal.model.Userchoice;
import io.camunda.demo.pick_animal.service.PickAnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PickAnimalControllerTest {

    @Mock
    private PickAnimalService pickAnimalService;

    @InjectMocks
    private PickAnimalController pickAnimalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserchoiceById_Found() {
        // Arrange
        String id = "123";
        Userchoice userchoice = new Userchoice(id, "John", new Date(), "Cat", "jpg", "image_url", "base64Image");
        when(pickAnimalService.findUserchoiceById(id)).thenReturn(Optional.of(userchoice));

        // Act
        Optional<Userchoice> result = pickAnimalController.findUserchoiceById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userchoice, result.get());
    }

    @Test
    void testFindUserchoiceById_NotFound() {
        // Arrange
        String id = "123";
        when(pickAnimalService.findUserchoiceById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Userchoice> result = pickAnimalController.findUserchoiceById(id);

        // Assert
        assertFalse(result.isPresent());
    }
}
