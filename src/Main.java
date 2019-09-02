import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static int num = 0;


    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите абсолютный или относительный путь к JSON файлу:");
        String file = reader.readLine();
        Gson gson = new Gson();
        ArrayList<Company> companies = gson.fromJson(new FileReader(file), new TypeToken<List<Company>>() {
        }.getType());
        LocalDate today = LocalDate.now();
        System.out.println();


        System.out.println("Все имеющиеся компании в формате «Краткое название» = «Дата основания 17/01/98»:");
        companies.stream().map(company -> {
            try {
                return ("Краткое название «" + company.getName_short() + "» = " + "Дата основания " +
                        new SimpleDateFormat("dd/MM/yy")
                                .format(new SimpleDateFormat("yyyy-MM-dd").parse(company.getEgrul_date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(System.out::println);
        System.out.println();


        companies.stream().flatMap(company -> Stream.of(company.getSecurities()).filter(securities -> {
            String[] info = (securities.getDate_to()).split("-");
            LocalDate date = LocalDate.of(Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]));
            if (today.isAfter(date)) num++;
            return today.isAfter(date);
        })).map(securities -> (securities.getName_full() + ", дата истечение срока - " + securities.getDate_to()
                + " Code - '" + securities.getCode() + "'.")).forEach(System.out::println);
        System.out.println("Количество просроченных ценных ценных бумаг на сегодняшний день: " + num);
        System.out.println();


        System.out.println("Введите дату в формате «ДД.ММ.ГГГГ», «ДД.ММ.ГГ», «ДД/ММ/ГГГГ» и «ДД/ММ/ГГ», для того чтобы " +
                " узнать список организаций, основанных после неё:");
        num = 0;
        String str = reader.readLine().trim();
        String[] array = str.split("[-=+*().,;:?!/]+");

        try {
            int year = Integer.parseInt(array[2]);
            if (year < 1000)
                if (year > 19 && year < 100) {
                    year = 1900 + year;
                } else {

                    if (year < today.getYear() % 100) {
                        year = 2000 + year;
                    } else {
                        throw new Exception();
                    }
                }
            LocalDate enterDate = LocalDate.of(year,
                    Integer.parseInt(array[1]), Integer.parseInt(array[0]));
            System.out.println(enterDate);
            companies.stream().filter(company -> {
                String[] info = company.getEgrul_date().split("[-/]+");
                LocalDate egrulDate = LocalDate.of(Integer.parseInt(info[0]),
                        Integer.parseInt(info[1]), Integer.parseInt(info[2]));
                if (egrulDate.isAfter(enterDate)) num++;
                return egrulDate.isAfter(enterDate);
            }).map(company -> ("Компания: " + company.getName_short() + " создана в " +
                    company.getEgrul_date())).forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Введена неверная дата!!!");
        }


        System.out.println("Введите код валюты в формате EU, USD, RUB, для того, чтобы вывести " +
                " ценные бумаги в этой валюте.");
        num = 0;
        String str1 = reader.readLine().trim().toUpperCase();
        companies.stream().flatMap(company -> Stream.of(company.getSecurities()).filter(securities -> {
            String string = securities.getCurrency().getCode();
            if (string.equals(str1)) num++;
            return string.equals(str1);
        })).map(securities -> ("ID = " + securities.getId() + ", Code - '" + securities.getCode() + "'."))
                  .forEach(System.out::println);
        if (num == 0) System.out.println("Подходящих ценных бумаг по запросу в списке нет!");
    }
}




