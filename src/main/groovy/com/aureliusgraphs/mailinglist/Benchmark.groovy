package com.aureliusgraphs.mailinglist

import com.thinkaurelius.titan.core.TitanGraph

import java.util.concurrent.Callable

/**
 *
 * @author Daniel Kuppitz (daniel at thinkaurelius.com)
 */
class Benchmark {

    public static Object[] measure(final TitanGraph g, final Callable<Object> closure, def loops = 100) {
        closure.call() // always ignore the first call
        def results = (1..loops).collect({
            g.commit()
            def t1 = System.currentTimeMillis()
            def res = closure.call()
            def t2 = System.currentTimeMillis()
            [res, t2 - t1]
        })
        def mean = results.collect({ it[1] }).mean()
        return [results.first()[0], mean]
    }
}
