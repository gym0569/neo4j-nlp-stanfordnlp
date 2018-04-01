package com.graphaware.nlp.integration;

import com.graphaware.nlp.StanfordNLPIntegrationTest;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CarModelNERTest extends StanfordNLPIntegrationTest {

    @Test
    public void testCustomNER() {
        String modelsPath = getClass().getClassLoader().getResource("").getPath();
        executeInTransaction("CALL ga.nlp.config.model.workdir({p0})", buildSeqParameters(modelsPath), emptyConsumer());
        String text = "Opel will be the name under which Vivaro will be placed, but if reception goes well then hopefully Chevy will carry the name in the states. The van has 5 cubic meteres of cargo space and can hold payloads up to 750 kg–meaning a great delivery van in the city. Lithium ion battery packs power the van and are mounted in the floor–similar to the Volt. Travel is expected to last up to 60 miles on electricity alone; after the gas-powered motor will extend the range to 250 miles.";

        String q = "CALL ga.nlp.processor.train({textProcessor: \"com.graphaware.nlp.processor.stanford.StanfordTextProcessor\", modelIdentifier: \"test-ner\", alg: \"ner\", inputFile: 'car-model-train.tsv', trainingParameters: {iter: 10}})";
        executeInTransaction(q, emptyConsumer());
        String t = "CALL ga.nlp.processor.test({textProcessor: \"com.graphaware.nlp.processor.stanford.StanfordTextProcessor\", modelIdentifier: \"test-ner\", alg: \"ner\", inputFile: 'nasa-test.tsv', trainingParameters: {iter: 10}})";
//        executeInTransaction(t, emptyConsumer());

        // Create pipeline
        String addPipelineQuery = "CALL ga.nlp.processor.addPipeline({textProcessor: 'com.graphaware.nlp.processor.stanford.StanfordTextProcessor', name: 'customNER', processingSteps: {tokenize: true, ner: true, sentiment: false, dependency: true, customNER: \"test-ner\"}})";
        executeInTransaction(addPipelineQuery, emptyConsumer());

        // Import some text
        executeInTransaction("CREATE (n:Document) SET n.text = {text}", Collections.singletonMap("text", text), emptyConsumer());

        String text2 = "We are convinced that we will get a fantastic reaction from the people who use such vehicles on a daily basis: Electric mobility will allow them to travel in city areas which are now off-limits to petrol and diesel-powered vehicles and the range-extender technology makes it possible to use an electric van for normal routine business,” says Chris Lacey, Executive Director, International Operations Opel/Vauxhall Commercial Vehicles.";
        executeInTransaction("CREATE (n:Document) SET n.text = {text}", Collections.singletonMap("text", text2), emptyConsumer());

        // Annotate
        executeInTransaction("MATCH (n:Document) CALL ga.nlp.annotate({text: n.text, id:id(n), pipeline: 'customNER', checkLanguage:false}) YIELD result MERGE (n)-[:HAS_ANNOTATED_TEXT]->(result)", emptyConsumer());

        executeInTransaction("MATCH (n:Tag) RETURN [x IN labels(n) | x] AS labels, n.value AS val", (result -> {
            while (result.hasNext()) {
                Map<String, Object> record = result.next();
                List<String> labels = (List<String>) record.get("labels");
                System.out.println(labels);
                System.out.println(record.get("val").toString());
            }
        }));
    }
}