/*
 * Copyright (c) 2013-2016 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.graphaware.nlp.enrich.conceptnet5;

import com.graphaware.nlp.NLPManager;
import com.graphaware.nlp.StanfordNLPIntegrationTest;
import com.graphaware.nlp.domain.Tag;
import com.graphaware.nlp.dsl.request.PipelineSpecification;
import com.graphaware.nlp.processor.TextProcessor;
import com.graphaware.nlp.util.ServiceLoader;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class
ConceptNet5Test extends StanfordNLPIntegrationTest {
    
    public static final String TEXT_PROCESSOR = "com.graphaware.nlp.processor.stanford.StanfordTextProcessor";

    @Test
    public void testImportHierarchy() {

        createPipeline(TEXT_PROCESSOR, "concept", "tokenize");
        PipelineSpecification pipelineSpecification = NLPManager.getInstance().getTextProcessorsManager().getPipelineSpecification("concept");
        TextProcessor textProcessor = getNLPManager().getTextProcessorsManager().getTextProcessor(TEXT_PROCESSOR);
        //ConceptNet5Importer instance = new ConceptNet5Importer.Builder("http://conceptnet5.media.mit.edu/data/5.4", textProcessor).build();
        ConceptNet5Importer instance = new ConceptNet5Importer.Builder("http://api.conceptnet.io").build();
        String lang = "en";
        Tag source = textProcessor.annotateTag("circuit", pipelineSpecification);
        List<Tag> result = instance.importHierarchy(source, ConceptNet5Enricher.RelDirection.OUT, true, Arrays.asList("en"), 2, Arrays.asList("IsA"), Arrays.asList("NN"), 50, 0.0d);
        assertTrue(result.size() > 0);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
