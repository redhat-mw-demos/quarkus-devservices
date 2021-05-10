package com.redhat.repository;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.domain.Fruit;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class FruitRepository implements PanacheRepository<Fruit> {
	public Uni<Fruit> findByName(String name) {
		return find("name", name).firstResult();
	}
}
