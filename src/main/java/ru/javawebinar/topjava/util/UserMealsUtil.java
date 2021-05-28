package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        meals.sort(Comparator.comparing(UserMeal::getDateTime));
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> filteredlist = new ArrayList<>();
        Map<LocalDate, Boolean> exceededDays = getMapExceededLocalDate(meals, caloriesPerDay);

        for (UserMeal userMeal : meals) {
            LocalTime localTime = LocalTime.of(userMeal.getDateTime().getHour(), userMeal.getDateTime().getMinute());
            if (startTime.isBefore(localTime) && endTime.isAfter(localTime)) {
                filteredlist.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), exceededDays.getOrDefault(getLocalDateByLocalDateTime(userMeal.getDateTime()), false)));
            }
        }
        return filteredlist;
    }

    public static HashMap<LocalDate,Boolean> getMapExceededLocalDate(List<UserMeal> meals, int caloriesPerDay){
        HashMap<LocalDate, Boolean> exceededDays = new HashMap<>();
        LocalDate localDate = null;
        int calories = 0;
        for (UserMeal userMeal : meals){
            if (localDate==null||!localDate.equals(getLocalDateByLocalDateTime(userMeal.getDateTime()))){
                localDate = getLocalDateByLocalDateTime(userMeal.getDateTime());
                calories = 0;
                calories+= userMeal.getCalories();
            }else {
                calories+= userMeal.getCalories();
                if (calories>caloriesPerDay){
                    exceededDays.put(localDate, true);
                }
            }
        }return exceededDays;
    }


    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Boolean> exceededDays = getMapExceededLocalDate(meals, caloriesPerDay);
        List<UserMeal> filteredList = meals.stream().filter(meal-> LocalTime.of(meal.getDateTime().getHour(), meal.getDateTime().getMinute()).isAfter(startTime)&&LocalTime.of(meal.getDateTime().getHour(), meal.getDateTime().getMinute()).isBefore(endTime)).collect(Collectors.toList());
        List<UserMealWithExcess> d= new ArrayList<>();
        filteredList.forEach(meal-> d.add(new UserMealWithExcess(meal.getDateTime(),meal.getDescription(),meal.getCalories(),exceededDays.getOrDefault(getLocalDateByLocalDateTime(meal.getDateTime()), false))));
        return d;
    }

    public static LocalDate getLocalDateByLocalDateTime(LocalDateTime localDateTime){
        return LocalDate.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth());
    }
}
