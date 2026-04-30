package com.example.helpers.builders;

import com.example.api.models.message.NotificationMpValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class NotificationMpValueBuilder implements BuilderInterface<NotificationMpValue> {

    private NotificationMpValue notificationMpValue;

    private Integer measurePointId;
    private Integer trigType;
    private Integer absolute;
    private Integer percent;

    private Double lmaxChannel;
    private Double leqChannel;
    private Double lasChannel;
    private Double lafChannel;
    private Double laeqChannel;
    private Double laeqRollingChannel;
    private Double laeqAccuChannel;
    private Double l90Channel;
    private Double l50Channel;
    private Double l10Channel;
    private Double lnChannel;
    private Double vChannel;
    private Double lChannel;
    private Double tChannel;
    private Double rChannel;
    private Double rVChannel;
    private Double rLChannel;
    private Double rTChannel;
    private Double vdvVChannel;
    private Double vdvLChannel;
    private Double vdvTChannel;
    private Double vdvVaccuChannel;
    private Double vdvLaccuChannel;
    private Double vdvTaccuChannel;

//    private Double lCustom; // deprecated, is now set in agenda


    public enum TriggType {
        TRIGGER, ABSOLUTE
    }

    public NotificationMpValueBuilder withAbsolute(final Integer absolute) {
        this.absolute = absolute;
        return this;
    }

    public NotificationMpValueBuilder withPercent(final Integer percent) {
        this.percent = percent;
        return this;
    }

    public NotificationMpValueBuilder withMeasurePointId(final Integer measurePointId) {
        this.measurePointId = measurePointId;
        return this;
    }

    public NotificationMpValueBuilder givenTrigType(final TriggType type) {
        this.trigType = switch (type) {
            case TRIGGER -> 0;
            case ABSOLUTE -> 1;
        };
        return this;
    }

    // Validates that the trigger type is ABSOLUTE (1) before setting a channel value
    private void validateAbsoluteTrigger(String fieldName) {
        if (trigType != 1) {
            throw new IllegalArgumentException(fieldName + " is used only with messagetype absolute which is set with \"withTrigType(ABSOLUTE)\"");
        }
    }

    public NotificationMpValueBuilder thenLmax(final Double lmaxChannel) {
        validateAbsoluteTrigger("lmaxChannel");
        this.lmaxChannel = lmaxChannel;
        return this;
    }

    public NotificationMpValueBuilder thenLeq(final Double leqChannel) {
        validateAbsoluteTrigger("leqChannel");
        this.leqChannel = leqChannel;
        return this;
    }

    public NotificationMpValueBuilder thenLas(final Double lasChannel) {
        validateAbsoluteTrigger("lasChannel");
        this.lasChannel = lasChannel;
        return this;
    }

    public NotificationMpValueBuilder thenLaf(final Double lafChannel) {
        validateAbsoluteTrigger("lafChannel");
        this.lafChannel = lafChannel;
        return this;
    }

    public NotificationMpValueBuilder thenLaeq(final Double laeqChannel) {
        validateAbsoluteTrigger("laeqChannel");
        this.laeqChannel = laeqChannel;
        return this;
    }

    public NotificationMpValueBuilder thenLaeqRolling(final Double laeqRollingChannel) {
        validateAbsoluteTrigger("laeqRollingChannel");
        this.laeqRollingChannel = laeqRollingChannel;
        return this;
    }

    public NotificationMpValueBuilder thenLaeqAccu(final Double laeqAccuChannel) {
        validateAbsoluteTrigger("laeqAccuChannel");
        this.laeqAccuChannel = laeqAccuChannel;
        return this;
    }

    public NotificationMpValueBuilder thenL90(final Double l90Channel) {
        validateAbsoluteTrigger("l90Channel");
        this.l90Channel = l90Channel;
        return this;
    }

    public NotificationMpValueBuilder thenL50(final Double l50Channel) {
        validateAbsoluteTrigger("l50Channel");
        this.l50Channel = l50Channel;
        return this;
    }

    public NotificationMpValueBuilder thenL10(final Double l10Channel) {
        validateAbsoluteTrigger("l10Channel");
        this.l10Channel = l10Channel;
        return this;
    }

    public NotificationMpValueBuilder thenLn(final Double lnChannel) {
        validateAbsoluteTrigger("lnChannel");
        this.lnChannel = lnChannel;
        return this;
    }

    public NotificationMpValueBuilder thenV(final Double vChannel) {
        validateAbsoluteTrigger("vChannel");
        this.vChannel = vChannel;
        return this;
    }

    public NotificationMpValueBuilder thenL(final Double lChannel) {
        validateAbsoluteTrigger("lChannel");
        this.lChannel = lChannel;
        return this;
    }

    public NotificationMpValueBuilder thenT(final Double tChannel) {
        validateAbsoluteTrigger("tChannel");
        this.tChannel = tChannel;
        return this;
    }

    public NotificationMpValueBuilder thenR(final Double rChannel) {
        validateAbsoluteTrigger("rChannel");
        this.rChannel = rChannel;
        return this;
    }

    public NotificationMpValueBuilder thenRv(final Double rVChannel) {
        validateAbsoluteTrigger("rVChannel");
        this.rVChannel = rVChannel;
        return this;
    }

    public NotificationMpValueBuilder thenRl(final Double rLChannel) {
        validateAbsoluteTrigger("rLChannel");
        this.rLChannel = rLChannel;
        return this;
    }

    public NotificationMpValueBuilder thenRt(final Double rTChannel) {
        validateAbsoluteTrigger("rTChannel");
        this.rTChannel = rTChannel;
        return this;
    }

    public NotificationMpValueBuilder thenVdvV(final Double vdvVChannel) {
        validateAbsoluteTrigger("vdvVChannel");
        this.vdvVChannel = vdvVChannel;
        return this;
    }

    public NotificationMpValueBuilder thenVdvL(final Double vdvLChannel) {
        validateAbsoluteTrigger("vdvLChannel");
        this.vdvLChannel = vdvLChannel;
        return this;
    }

    public NotificationMpValueBuilder thenVdvT(final Double vdvTChannel) {
        validateAbsoluteTrigger("vdvTChannel");
        this.vdvTChannel = vdvTChannel;
        return this;
    }

    public NotificationMpValueBuilder thenVdvVaccu(final Double vdvVaccuChannel) {
        validateAbsoluteTrigger("vdvVaccuChannel");
        this.vdvVaccuChannel = vdvVaccuChannel;
        return this;
    }

    public NotificationMpValueBuilder thenVdvLaccu(final Double vdvLaccuChannel) {
        validateAbsoluteTrigger("vdvLaccuChannel");
        this.vdvLaccuChannel = vdvLaccuChannel;
        return this;
    }

    public NotificationMpValueBuilder thenVdvTaccu(final Double vdvTaccuChannel) {
        validateAbsoluteTrigger("vdvTaccuChannel");
        this.vdvTaccuChannel = vdvTaccuChannel;
        return this;
    }

    @Override
    public void build() {
        if (notificationMpValue == null) {
            notificationMpValue = new NotificationMpValue();
        }
        if (measurePointId != null) {
            notificationMpValue.setMeasurePointId(measurePointId);
        }
        if (trigType != null) {
            notificationMpValue.setTrigType(trigType);
        }
        if (absolute != null) {
            notificationMpValue.setAbsolute(absolute);
        }
        if (percent != null) {
            notificationMpValue.setPercent(percent);
        }
        if (lmaxChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", lmaxChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("Lmax", properties);
        }

        if (leqChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", leqChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("Leq", properties);
        }

        if (lasChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", lasChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("LAS", properties);
        }

        if (lafChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", lafChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("LAF", properties);
        }

        if (laeqChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", laeqChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("LAeq", properties);
        }

        if (laeqRollingChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", laeqRollingChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("LAeq-rolling", properties);
        }

        if (laeqAccuChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", laeqAccuChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("LAeq-accumulated", properties);
        }

        if (l90Channel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", l90Channel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("L90", properties);
        }

        if (l50Channel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", l50Channel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("L50", properties);
        }

        if (l10Channel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", l10Channel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("L10", properties);
        }

        if (lnChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", lnChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("Ln", properties);
        }

        if (vChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", vChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("V", properties);
        }

        if (lChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", lChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("L", properties);
        }

        if (tChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", tChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("T", properties);
        }

        if (rChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", rChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("R", properties);
        }

        if (rVChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", rVChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("rV", properties);
        }

        if (rLChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", rLChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("rL", properties);
        }

        if (rTChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", rTChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("rT", properties);
        }

        if (vdvVChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", vdvVChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("VDV-V", properties);
        }

        if (vdvLChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", vdvLChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("VDV-L", properties);
        }

        if (vdvTChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", vdvTChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("VDV-T", properties);
        }

        if (vdvVaccuChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", vdvVaccuChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("VDV-V accu", properties);
        }

        if (vdvLaccuChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", vdvLaccuChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("VDV-L accu", properties);
        }

        if (vdvTaccuChannel != null) {
            Map<String, Double> high = new HashMap<>();
            high.put("high", vdvTaccuChannel);

            Map<String, Map<String, Double>> properties = new HashMap<>();
            properties.put("properties", high);
            notificationMpValue.setChannels("VDV-T accu", properties);
        }
    }

    @Override
    public void setProvider(NotificationMpValue provider) {
        this.notificationMpValue = provider;
    }

    @Override
    public NotificationMpValue getProvider() {
        return notificationMpValue;
    }


    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(notificationMpValue);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
