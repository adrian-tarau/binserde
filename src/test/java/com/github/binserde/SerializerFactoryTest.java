package com.github.binserde;

import com.github.binserde.dto.Address;
import com.github.binserde.dto.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SerializerFactoryTest {

    private SerializerFactory factory;

    @BeforeEach
    public void setup() {
        factory = SerializerFactory.getInstance();
    }

    @Test
    void getInstance() {
        assertNotNull(factory);
    }

    @Test
    void register() {
        assertFalse(factory.isSupported(Person.class));
        factory.register(Person.class, Person.ID);
        assertTrue(factory.isSupported(Person.class));
    }

    @Test
    void registerWithWrongIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> factory.register(Person.class, -49));
        assertThrows(IllegalArgumentException.class, () -> factory.register(Person.class, 40000));
    }

    @Test
    void registerWithAlreadyRegisteredIdentifier() {
        factory.register(Person.class, Person.ID);
        assertThrows(IllegalArgumentException.class, () -> factory.register(Address.class, Person.ID));
    }

    @Test
    void isSupported() {
    }
}