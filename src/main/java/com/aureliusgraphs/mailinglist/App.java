package com.aureliusgraphs.mailinglist;

import com.thinkaurelius.titan.core.TitanGraph;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Daniel Kuppitz (daniel at thinkaurelius.com)
 */
public class App {

    public static void main(final String[] args) {

        System.out.println();
        System.out.println("Benchmark started, be patient...");
        System.out.println();

        final TitanGraph g = Vijay.createSampleGraph();
        final Object[] res1 = Benchmark.measure(g, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return Vijay.getTweetsAndUsers(g);
            }
        }, 100);
        final Object[] res2 = Benchmark.measure(g, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return Vijay.getTweetsAndUsersES(g);
            }
        }, 100);

        System.out.println("Mean time without ES: " + res1[1] + " ms");
        System.out.println("Mean time with ES: " + res2[1] + " ms");
        System.out.println();
        System.out.println("Results are" + (equalMaps((Map) res1[0], (Map) res2[0]) ? "" : " not") + " identical.");
        System.out.println();
        System.out.println(res1[0].toString().substring(0, 200) + "...");
        System.out.println();
    }

    private static boolean equalMaps(final Map m1, final Map m2) {
        if (m1.size() != m2.size()) return false;
        for (Object key : m1.keySet()) {
            if (!m1.get(key).equals(m2.get(key))) return false;
        }
        return true;
    }
}
