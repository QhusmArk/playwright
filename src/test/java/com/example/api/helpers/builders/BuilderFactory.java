package com.example.api.helpers.builders;

import java.util.function.Function;

public class BuilderFactory {

    public static <T extends BuilderInterface<?>> T getBuilder(Providers providers, Class<T> clazz) {
        Function<Object, T> safeCast = (builder) -> {
            if (clazz.isInstance(builder)) {
                return clazz.cast(builder);
            } else {
                throw new IllegalArgumentException("Cannot cast " + builder.getClass() + " to " + clazz);
            }
        };
        Object builder = null;

        if (providers.equals(Providers.AGENDA)) {
            builder = new AgendaBuilder();
        } else if (providers.equals(Providers.BLAST)) {
            builder = new BlastBuilder();
        } else if (providers.equals(Providers.COMMENT)) {
            builder = new CommentBuilder();
        }
//        else if (providers.equals(Providers.DATAREPORT)) {
//            builder = new DataReportBuilder();
//        }
        else if (providers.equals(Providers.DEVICE)) {
            builder = new ChangeBuilder();
        } else if (providers.equals(Providers.MESSAGE_RULE)) {
            builder = new MessageRuleBuilder();
        } else if (providers.equals(Providers.NOTIFICATION_MP_VALUE)) {
            builder = new NotificationMpValueBuilder();
        } else if (providers.equals(Providers.MP)) {
            builder = new MeasuringPointBuilder();
        } else if (providers.equals(Providers.PROJECT)) {
            builder = new ProjectBuilder();
        } else if (providers.equals(Providers.USER)) {
            builder = new UserBuilder();
        } else if (providers.equals(Providers.SCHEDULED_REPORT)) {
            builder = new ScheduledReportBuilder();
        } else if (providers.equals(Providers.SEARCH)) {
            builder = new SearchBuilder();
        } else if (providers.equals(Providers.ANALYSIS_PAYLOAD)) {
            builder = new AnalysisPayloadBuilder();
        } else if (providers.equals(Providers.BILLING_REPORT)) {
            builder = new BillingReportBuilder();
        }
        return safeCast.apply(builder);
    }

    public enum Providers {
        AGENDA, BLAST, COMMENT, DATAREPORT, DEVICE, MESSAGE_RULE, NOTIFICATION_MP_VALUE, MP, PROJECT,
        SCHEDULED_REPORT, USER, SEARCH, ANALYSIS_PAYLOAD, BILLING_REPORT
    }
}
