package com.example.api.helpers.builders;

import com.example.api.models.report.analysis.AnalysisPayload;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AnalysisPayloadBuilder implements BuilderInterface<AnalysisPayload> {

    private AnalysisPayload analysisPayload;

    private AnalysisPayload.FrequencyOperation freqOp;
    private AnalysisPayload.Standard standard;
    private Integer highPass;
    private Integer lowPass;
    private String timeOp;

    public AnalysisPayloadBuilder givenFreqOp() {
        this.freqOp = AnalysisPayload.FrequencyOperation.builder().build();
        return this;
    }

    public AnalysisPayloadBuilder thenOperation(final String operation) {
        if (freqOp == null) {
            throw new IllegalStateException("Method \"givenFreqOp\" must be set first!");
        }
        this.freqOp.setOperation(operation);
        return this;
    }

    public AnalysisPayloadBuilder thenBw(final String bw) {
        if (freqOp == null) {
            throw new IllegalStateException("Method \"givenFreqOp\" must be set first!");
        }
        this.freqOp.setBw(bw);
        return this;
    }

    public AnalysisPayloadBuilder thenResp(final String resp) {
        if (freqOp == null) {
            throw new IllegalStateException("Method \"givenFreqOp\" must be set first!");
        }
        this.freqOp.setResp(resp);
        return this;
    }

    public AnalysisPayloadBuilder thenFftType(final String fftType) {
        if (freqOp == null) {
            throw new IllegalStateException("Method \"givenFreqOp\" must be set first!");
        }
        this.freqOp.setFftType(fftType);
        return this;
    }

    public AnalysisPayloadBuilder thenFftWin(final String fftWin) {
        if (freqOp == null) {
            throw new IllegalStateException("Method \"givenFreqOp\" must be set first!");
        }
        this.freqOp.setFftWin(fftWin);
        return this;
    }

    public AnalysisPayloadBuilder thenQ(final int q) {
        if (freqOp == null) {
            throw new IllegalStateException("Method \"givenFreqOp\" must be set first!");
        }
        this.freqOp.setQ(q);
        return this;
    }

    public AnalysisPayloadBuilder givenStandard() {
        this.standard = AnalysisPayload.Standard.builder().build();
        return this;
    }

    public AnalysisPayloadBuilder thenStandardId(final String standardId) {
        if (standard == null) {
            throw new IllegalStateException("Method \"givenStandard\" must be set first!");
        }
        this.standard.setStandardId(standardId);
        return this;
    }

    public AnalysisPayloadBuilder thenNoRms(final boolean noRms) {
        if (standard == null) {
            throw new IllegalStateException("Method \"givenStandard\" must be set first!");
        }
        this.standard.setNoRms(noRms);
        return this;
    }

    public AnalysisPayloadBuilder thenHighPass(final int highPass) {
        this.highPass = highPass;
        return this;
    }

    public AnalysisPayloadBuilder thenLowPass(final int lowPass) {
        this.lowPass = lowPass;
        return this;
    }

    public AnalysisPayloadBuilder thenTimeOp(final String timeOp) {
        this.timeOp = timeOp;
        return this;
    }

    @Override
    public void build() {
        if (analysisPayload == null) {
            analysisPayload = new AnalysisPayload();
        }
        if (standard != null) {
            analysisPayload.setStandard(standard);
        }
        if (freqOp != null) {
            analysisPayload.setFreqOp(freqOp);
        }
        if (highPass != null) {
            analysisPayload.setHighPass(highPass);
        }
        if (lowPass != null) {
            analysisPayload.setLowPass(lowPass);
        }
        if (timeOp != null) {
            analysisPayload.setTimeOp(timeOp);
        }
    }

    @Override
    public AnalysisPayload getProvider() {
        return analysisPayload;
    }

    @Override
    public void setProvider(AnalysisPayload provider) {
        this.analysisPayload = provider;
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(analysisPayload);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
