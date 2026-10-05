package com.yarukov.is_lab_1.repository;

import com.yarukov.is_lab_1.model.City;
import com.yarukov.is_lab_1.model.Coordinates;
import com.yarukov.is_lab_1.model.Human;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
public class CityRepository implements Serializable {

    @Inject
    private EntityManager em;


    public void create(City city) {
        executeInTransaction(() -> em.persist(city));
    }


    public Optional<City> findById(Long id) {
        return Optional.ofNullable(em.find(City.class, id));
    }

    public City update(City city) {
        return executeInTransactionWithResult(() -> em.merge(city));
    }

    public void delete(Long id) {
        executeInTransaction(() -> {
            City city = em.find(City.class, id);
            if (city != null) {
                em.remove(city);
            }
        });
    }

    public List<City> findAll() {
        return em.createQuery("SELECT c FROM City c ORDER BY c.id ASC", City.class)
                .getResultList();
    }


    public List<Coordinates> findAllCoordinates() {
        return em.createQuery("SELECT c FROM Coordinates c ORDER BY c.id ASC", Coordinates.class)
                .getResultList();
    }


    public void createCoordinates(Coordinates coordinates) {
        executeInTransaction(() -> em.persist(coordinates));
    }

    public List<Human> findAllHumans() {
        return em.createQuery("SELECT h FROM Human h ORDER BY h.id ASC", Human.class)
                .getResultList();
    }


    public void createHuman(Human human) {
        executeInTransaction(() -> em.persist(human));
    }


    private void executeInTransaction(Runnable action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.run();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    private <T> T executeInTransactionWithResult(Supplier<T> action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = action.get();
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }





}