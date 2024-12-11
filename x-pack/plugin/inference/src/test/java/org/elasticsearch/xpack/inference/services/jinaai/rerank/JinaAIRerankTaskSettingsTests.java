/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.inference.services.jinaai.rerank;

import org.elasticsearch.common.ValidationException;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.test.AbstractWireSerializingTestCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;

public class JinaAIRerankTaskSettingsTests extends AbstractWireSerializingTestCase<JinaAIRerankTaskSettings> {

    public static JinaAIRerankTaskSettings createRandom() {
        var returnDocuments = randomBoolean() ? randomBoolean() : null;
        var topNDocsOnly = randomBoolean() ? randomIntBetween(1, 10) : null;
        var maxChunksPerDoc = randomBoolean() ? randomIntBetween(1, 20) : null;

        return new JinaAIRerankTaskSettings(topNDocsOnly, returnDocuments, maxChunksPerDoc);
    }

    public void testFromMap_WithValidValues_ReturnsSettings() {
        Map<String, Object> taskMap = Map.of(
            JinaAIRerankTaskSettings.RETURN_DOCUMENTS,
            true,
            JinaAIRerankTaskSettings.TOP_N_DOCS_ONLY,
            5,
            JinaAIRerankTaskSettings.MAX_CHUNKS_PER_DOC,
            10
        );
        var settings = JinaAIRerankTaskSettings.fromMap(new HashMap<>(taskMap));
        assertTrue(settings.getReturnDocuments());
        assertEquals(5, settings.getTopNDocumentsOnly().intValue());
        assertEquals(10, settings.getMaxChunksPerDoc().intValue());
    }

    public void testFromMap_WithNullValues_ReturnsSettingsWithNulls() {
        var settings = JinaAIRerankTaskSettings.fromMap(Map.of());
        assertNull(settings.getReturnDocuments());
        assertNull(settings.getTopNDocumentsOnly());
        assertNull(settings.getMaxChunksPerDoc());
    }

    public void testFromMap_WithInvalidReturnDocuments_ThrowsValidationException() {
        Map<String, Object> taskMap = Map.of(
            JinaAIRerankTaskSettings.RETURN_DOCUMENTS,
            "invalid",
            JinaAIRerankTaskSettings.TOP_N_DOCS_ONLY,
            5,
            JinaAIRerankTaskSettings.MAX_CHUNKS_PER_DOC,
            10
        );
        var thrownException = expectThrows(ValidationException.class, () -> JinaAIRerankTaskSettings.fromMap(new HashMap<>(taskMap)));
        assertThat(thrownException.getMessage(), containsString("field [return_documents] is not of the expected type"));
    }

    public void testFromMap_WithInvalidTopNDocsOnly_ThrowsValidationException() {
        Map<String, Object> taskMap = Map.of(
            JinaAIRerankTaskSettings.RETURN_DOCUMENTS,
            true,
            JinaAIRerankTaskSettings.TOP_N_DOCS_ONLY,
            "invalid",
            JinaAIRerankTaskSettings.MAX_CHUNKS_PER_DOC,
            10
        );
        var thrownException = expectThrows(ValidationException.class, () -> JinaAIRerankTaskSettings.fromMap(new HashMap<>(taskMap)));
        assertThat(thrownException.getMessage(), containsString("field [top_n] is not of the expected type"));
    }

    public void testFromMap_WithInvalidMaxChunksPerDoc_ThrowsValidationException() {
        Map<String, Object> taskMap = Map.of(
            JinaAIRerankTaskSettings.RETURN_DOCUMENTS,
            true,
            JinaAIRerankTaskSettings.TOP_N_DOCS_ONLY,
            5,
            JinaAIRerankTaskSettings.MAX_CHUNKS_PER_DOC,
            "invalid"
        );
        var thrownException = expectThrows(ValidationException.class, () -> JinaAIRerankTaskSettings.fromMap(new HashMap<>(taskMap)));
        assertThat(thrownException.getMessage(), containsString("field [max_chunks_per_doc] is not of the expected type"));
    }

    public void UpdatedTaskSettings_WithEmptyMap_ReturnsSameSettings() {
        var initialSettings = new JinaAIRerankTaskSettings(5, true, 10);
        JinaAIRerankTaskSettings updatedSettings = (JinaAIRerankTaskSettings) initialSettings.updatedTaskSettings(Map.of());
        assertEquals(initialSettings, updatedSettings);
    }

    public void testUpdatedTaskSettings_WithNewReturnDocuments_ReturnsUpdatedSettings() {
        var initialSettings = new JinaAIRerankTaskSettings(5, true, 10);
        Map<String, Object> newSettings = Map.of(JinaAIRerankTaskSettings.RETURN_DOCUMENTS, false);
        JinaAIRerankTaskSettings updatedSettings = (JinaAIRerankTaskSettings) initialSettings.updatedTaskSettings(newSettings);
        assertFalse(updatedSettings.getReturnDocuments());
        assertEquals(initialSettings.getTopNDocumentsOnly(), updatedSettings.getTopNDocumentsOnly());
        assertEquals(initialSettings.getMaxChunksPerDoc(), updatedSettings.getMaxChunksPerDoc());
    }

    public void testUpdatedTaskSettings_WithNewTopNDocsOnly_ReturnsUpdatedSettings() {
        var initialSettings = new JinaAIRerankTaskSettings(5, true, 10);
        Map<String, Object> newSettings = Map.of(JinaAIRerankTaskSettings.TOP_N_DOCS_ONLY, 7);
        JinaAIRerankTaskSettings updatedSettings = (JinaAIRerankTaskSettings) initialSettings.updatedTaskSettings(newSettings);
        assertEquals(7, updatedSettings.getTopNDocumentsOnly().intValue());
        assertEquals(initialSettings.getReturnDocuments(), updatedSettings.getReturnDocuments());
        assertEquals(initialSettings.getMaxChunksPerDoc(), updatedSettings.getMaxChunksPerDoc());
    }

    public void testUpdatedTaskSettings_WithNewMaxChunksPerDoc_ReturnsUpdatedSettings() {
        var initialSettings = new JinaAIRerankTaskSettings(5, true, 10);
        Map<String, Object> newSettings = Map.of(JinaAIRerankTaskSettings.MAX_CHUNKS_PER_DOC, 15);
        JinaAIRerankTaskSettings updatedSettings = (JinaAIRerankTaskSettings) initialSettings.updatedTaskSettings(newSettings);
        assertEquals(15, updatedSettings.getMaxChunksPerDoc().intValue());
        assertEquals(initialSettings.getReturnDocuments(), updatedSettings.getReturnDocuments());
        assertEquals(initialSettings.getTopNDocumentsOnly(), updatedSettings.getTopNDocumentsOnly());
    }

    public void testUpdatedTaskSettings_WithMultipleNewValues_ReturnsUpdatedSettings() {
        var initialSettings = new JinaAIRerankTaskSettings(5, true, 10);
        Map<String, Object> newSettings = Map.of(
            JinaAIRerankTaskSettings.RETURN_DOCUMENTS,
            false,
            JinaAIRerankTaskSettings.TOP_N_DOCS_ONLY,
            7,
            JinaAIRerankTaskSettings.MAX_CHUNKS_PER_DOC,
            15
        );
        JinaAIRerankTaskSettings updatedSettings = (JinaAIRerankTaskSettings) initialSettings.updatedTaskSettings(newSettings);
        assertFalse(updatedSettings.getReturnDocuments());
        assertEquals(7, updatedSettings.getTopNDocumentsOnly().intValue());
        assertEquals(15, updatedSettings.getMaxChunksPerDoc().intValue());
    }

    @Override
    protected Writeable.Reader<JinaAIRerankTaskSettings> instanceReader() {
        return JinaAIRerankTaskSettings::new;
    }

    @Override
    protected JinaAIRerankTaskSettings createTestInstance() {
        return createRandom();
    }

    @Override
    protected JinaAIRerankTaskSettings mutateInstance(JinaAIRerankTaskSettings instance) throws IOException {
        return randomValueOtherThan(instance, JinaAIRerankTaskSettingsTests::createRandom);
    }
}
