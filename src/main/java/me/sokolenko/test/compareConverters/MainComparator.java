package me.sokolenko.test.compareConverters;

import me.sokolenko.test.compareConverters.dozer.DozerConverter;
import me.sokolenko.test.compareConverters.manual.ManualConverter;
import me.sokolenko.test.compareConverters.mapstruct.MapStructConverterImpl;
import me.sokolenko.test.compareConverters.model.source.Category;
import me.sokolenko.test.compareConverters.orika.OrikaConverter;
import org.testng.annotations.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainComparator {

    private static final List<Class> converterClassList = new ArrayList<Class>(){
        {
            add(DozerConverter.class);
            add(OrikaConverter.class);
            add(ManualConverter.class);
            add(MapStructConverterImpl.class);
        }
    };

    @Test
    public void test() {
        List<Converter> converters = getConverters();
        int iterationsCount = 1;

        PodamFactory randomiser = new PodamFactoryImpl();
        Category category = randomiser.manufacturePojo(Category.class);
        int[] latencies = new int[iterationsCount];

        for(Converter converter : converters) {
            for (int i = 0; i < iterationsCount; i++) {
                long start = System.nanoTime();

                me.sokolenko.test.compareConverters.model.target.Category result = converter.map(category);

                long end = System.nanoTime();

                latencies[i] = (int) (end - start);
            }

            printResults(converter, latencies);
        }
    }

    private static void printResults(Converter converter, int[] latencies) {
        System.out.println("Converter: " + converter.getClass().getSimpleName());
        Arrays.sort(latencies);

        System.out.println("Min " + latencies[0]);
        System.out.println("Mid " + latencies[latencies.length / 2]);
        System.out.println("90% " + latencies[((int) (latencies.length * 0.9))]);
        System.out.println("95% " + latencies[((int) (latencies.length * 0.95))]);
        System.out.println("99% " + latencies[((int) (latencies.length * 0.99))]);
        System.out.println("99.9% " + latencies[((int) (latencies.length * 0.999))]);
        System.out.println("99.99% " + latencies[((int) (latencies.length * 0.9999))]);
        System.out.println("99.999% " + latencies[((int) (latencies.length * 0.99999))]);
        System.out.println("99.9999% " + latencies[((int) (latencies.length * 0.999999))]);
        System.out.println("Max " + latencies[latencies.length - 1]);

        int total = 0;
        for(int i=0; i<latencies.length; i++) {
            total += latencies[i];
        }

        System.out.println("Avg "+ (total / latencies.length));
    }

    private static List<Converter> getConverters() {
        List<Converter> converterList = new ArrayList<>();

        for(Class converterClass :  converterClassList){
            Converter converter = null;
            try {
                converter = (Converter) converterClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            converterList.add(converter);
        }
        return converterList;
    }
}
