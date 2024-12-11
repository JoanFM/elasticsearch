/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.inference.external.request.jinaai;

import org.elasticsearch.core.Nullable;
import org.elasticsearch.inference.InputType;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xpack.inference.services.jinaai.JinaAIServiceSettings;
import org.elasticsearch.xpack.inference.services.jinaai.embeddings.JinaAIEmbeddingsTaskSettings;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.xpack.inference.services.jinaai.embeddings.JinaAIEmbeddingsTaskSettings.invalidInputTypeMessage;

public record JinaAIEmbeddingsRequestEntity(
    List<String> input,
    JinaAIEmbeddingsTaskSettings taskSettings,
    @Nullable String model
) implements ToXContentObject {

    private static final String SEARCH_DOCUMENT = "retrieval.passage";
    private static final String SEARCH_QUERY = "retrieval.query";
    private static final String CLUSTERING = "separation";
    private static final String CLASSIFICATION = "classification";
    private static final String INPUTS_FIELD = "inputs";
    public static final String TASK_TYPE_FIELD = "task";

    public JinaAIEmbeddingsRequestEntity {
        Objects.requireNonNull(input);
        Objects.requireNonNull(taskSettings);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(INPUTS_FIELD, input);
        if (model != null) {
            builder.field(JinaAIServiceSettings.OLD_MODEL_ID_FIELD, model);
        }

        if (taskSettings.getInputType() != null) {
            builder.field(TASK_TYPE_FIELD, covertToString(taskSettings.getInputType()));
        }

        builder.endObject();
        return builder;
    }

    // default for testing
    static String covertToString(InputType inputType) {
        return switch (inputType) {
            case INGEST -> SEARCH_DOCUMENT;
            case SEARCH -> SEARCH_QUERY;
            case CLASSIFICATION -> CLASSIFICATION;
            case CLUSTERING -> CLUSTERING;
            default -> {
                assert false : invalidInputTypeMessage(inputType);
                yield null;
            }
        };
    }
}
