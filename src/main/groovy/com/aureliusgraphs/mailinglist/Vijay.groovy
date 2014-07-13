package com.aureliusgraphs.mailinglist

import com.thinkaurelius.titan.core.TitanFactory
import com.thinkaurelius.titan.core.TitanGraph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.gremlin.groovy.Gremlin
import org.apache.commons.configuration.BaseConfiguration

import static com.tinkerpop.blueprints.Direction.OUT

/**
 *
 * @author Daniel Kuppitz (daniel at thinkaurelius.com)
 */
class Vijay {

    static {
        Gremlin.load()
    }

    public static TitanGraph createSampleGraph() {

        def conf = new BaseConfiguration()
        conf.setProperty("storage.backend", "cassandra")
        conf.setProperty("storage.hostname", "192.168.2.110")
        conf.setProperty("storage.index.search.backend", "elasticsearch")
        conf.setProperty("storage.index.search.hostname", "127.0.0.1")
        conf.setProperty("storage.index.search.client-only", true)
        conf.setProperty("storage.index.search.local-mode", false)

        def g = TitanFactory.open(conf)
        g.getType("type") ?: g.makeKey("type").dataType(String.class).indexed(Vertex.class).indexed("search", Vertex.class).make()
        g.getType("userId") ?: g.makeKey("userId").dataType(Integer.class).indexed(Vertex.class).make()
        g.getType("tweetId") ?: g.makeKey("tweetId").dataType(Integer.class).indexed(Vertex.class).make()
        g.getType("tweetedBy") ?: g.makeLabel("tweetedBy").make()
        g.commit()

        loadSampleGraph(g)
    }

    public static TitanGraph loadSampleGraph(final TitanGraph g) {

        def users = []; (1..200).each({ users << g.addVertex(["type": "user", "userId": it]) })
        def tweets = []; (1..2000).each({ tweets << g.addVertex(["type": "tweet", "tweetId": it]) })

        tweets.each({ tweet ->
            def user = users.get((int) Math.floor(Math.random() * users.size()))
            tweet.addEdge("tweetedBy", user)
        })

        g.commit()

        return g
    }

    public static Map getTweetsAndUsers(final TitanGraph g) {
        def tweetsAndUsers = g.multiQuery(g.V("type", "tweet").toList()).direction(OUT).labels("tweetedBy").vertices().collectEntries({
            k, v -> [k, v.iterator().next()]
        })
        def properties = g.multiQuery(tweetsAndUsers.keySet() + tweetsAndUsers.values()).keys("type",
                "tweetId", "userId").properties().collectEntries({ k, v ->
            [k, v.collect({
                [it.getPropertyKey().getName(),
                 it.getValue()]
            }).collectEntries()]
        })
        tweetsAndUsers.collectEntries({ k, v -> [properties[k], properties[v]] })
    }

    public static Map getTweetsAndUsersES(final TitanGraph g) {
        def searchResult = g.indexQuery("search", "v.type:tweet").vertices()
        def tweetsAndUsers = g.multiQuery(searchResult*.getElement()).direction(OUT).labels("tweetedBy").vertices().collectEntries({
            k, v -> [k, v.iterator().next()]
        })
        def properties = g.multiQuery(tweetsAndUsers.keySet() + tweetsAndUsers.values()).keys("type",
                "tweetId", "userId").properties().collectEntries({ k, v ->
            [k, v.collect({
                [it.getPropertyKey().getName(),
                 it.getValue()]
            }).collectEntries()]
        })
        tweetsAndUsers.collectEntries({ k, v -> [properties[k], properties[v]] })
    }
}
