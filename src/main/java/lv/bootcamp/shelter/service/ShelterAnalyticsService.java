package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.*;
import java.util.stream.Collectors;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        Set<String> uniqueSpecies = new TreeSet<>();
        Map<String, List<Animal>> animalsBySpecies = new HashMap<>();
        List<String> animalsNeedingVetInput = new ArrayList<>();

        // - uniqueSpecies
        for(Animal animal: allAnimals){
            uniqueSpecies.add(animal.getSpecies());
        }

        // - animalsBySpecies

        for(Animal animal: allAnimals){
            String species = animal.getSpecies();
            if(!animalsBySpecies.containsKey(species)){
                animalsBySpecies.put(species, new ArrayList<>());
            }
            animalsBySpecies.get(species).add(animal);
        }
        // - animalsNeedingVetInput with format name(species)

        for(Animal animal: allAnimals){
            if(animal.getAge() == null){
                animalsNeedingVetInput.add(animal.getName() + "(" + animal.getSpecies() + ")");
            }
        }

        // Use stream pipelines for:
        // - vaccinated vs unvaccinated counts per species

        Map<String, Long> vaccinatedPerSpecies = allAnimals.stream()
                .filter(Animal::isVaccinated)
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        Map<String, Long> unvaccinatedPerSpecies = allAnimals.stream()
                .filter(animal -> !animal.isVaccinated())
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        // - oldest animal per species (excluding unknown ages)

        Map<String, Optional<Animal>> oldestAnimalPerSpecies = allAnimals.stream()
                .filter(animal -> animal.getAge() != null)
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.maxBy(Comparator.comparing(Animal::getAge))));

        return new ShelterReportData(importResult, uniqueSpecies, animalsBySpecies, animalsNeedingVetInput,
                                        vaccinatedPerSpecies, unvaccinatedPerSpecies, oldestAnimalPerSpecies,
                                        importResult.importedRows(), importResult.skippedRows());


    }
}
