package com.example.api.helpers.builders;

import com.example.api.models.agenda.Agenda;
import com.example.api.models.agenda.Child;
import com.example.api.models.agenda.Definition;
import com.example.api.models.agenda.Label;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendaBuilder implements BuilderInterface<Agenda> {

    private Agenda agenda;
    private String name;
    private List<Label> labels;
    private List<Child> months;
    private List<Definition> definitions;
    private List<Child> weeks;
    private List<Child> weekdays;
    private List<Child> hours;
    private int[] startHour;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final LocalDateTime ldt = LocalDateTime.now().minusYears(1);
    private final String startDate = ldt.format(formatter);

    private int idCounter = 0;


    public AgendaBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public AgendaBuilder withLabels() {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        return this;
    }

    public AgendaBuilder withDefinitions() {
        if (definitions == null) {
            definitions = new ArrayList<>();
        }
        return this;
    }

    public AgendaBuilder givenDefinitions(final List<Definition> d) {
        if (this.definitions == null) {
            this.definitions = new ArrayList<>();
        }
        this.definitions.addAll(d);
        idCounter = idCounter + d.size();
        return this;
    }

    public AgendaBuilder givenLabels(final List<Label> l) {
        if (this.labels == null) {
            this.labels = new ArrayList<>();
        }
        this.labels.addAll(l);
        return this;
    }

    public AgendaBuilder givenLabel() {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        return this;
    }

    public AgendaBuilder givenLabel(final String name) {
        if (labels == null) {
            labels = new ArrayList<>();
            labels.add(Label.builder().name("default").id(0).build());
        }

        idCounter++;

        Label label = Label.builder()
                .name(name)
                .id(idCounter)
                .build();
        labels.add(label);
        return this;
    }

    public AgendaBuilder thenWeekday(final int[] occurrence) {
        if (weekdays == null) {
            weekdays = new ArrayList<>();
        }
        Child weekday = Child.builder()
                .periodType("weekday")
                .occurrence(occurrence)
                .build();
        weekdays.add(weekday);
        return this;
    }

    public AgendaBuilder thenStartHour(final int start) {
        this.startHour = new int[]{start};
        return this;
    }

    public AgendaBuilder thenDuration(final Integer duration) {
        if (hours == null) {
            hours = new ArrayList<>();
        }
        Child hour = Child.builder()
                .periodType("hour")
                .duration(duration)
                .occurrence(startHour)
                .build();
        hours.add(hour);

        buildDefinition();
        return this;
    }

    private void setMonth() {
        if (months == null) {
            months = new ArrayList<>();
        }
        Child month = Child.builder()
                .periodType("month")
                .repeatValue(1)
                .build();
        months.add(month);
    }

    private void setWeek() {
        if (weeks == null) {
            weeks = new ArrayList<>();
        }
        Child week = Child.builder()
                .periodType("week")
                .repeatValue(1)
                .build();
        weeks.add(week);
    }

    private void buildDefinition() {
        if (definitions == null) {
            definitions = new ArrayList<>();
        }

        setMonth();
        setWeek();

        weekdays.getFirst().setChild(hours);
        weeks.getFirst().setChild(weekdays);
        months.getFirst().setChild(weeks);

        Definition definition = Definition.builder()
                .periodType("year")
//                .start(startDate)
                .start("2001-01-01 00:00")
                .repeatValue(1)
                .label(idCounter)
                .child(months)
                .build();
        definitions.add(definition);

        months = null;
        weeks = null;
        weekdays = null;
        hours = null;
    }

    @Override
    public void build() {
        if (agenda == null) {
            agenda = new Agenda();
        }
        if (name != null) {
            agenda.setName(name);
        }
        if (labels != null) {
            agenda.setLabels(labels);
        }
        if (definitions != null) {
            agenda.setDefinitions(definitions);
        }
    }

    @Override
    public Agenda getProvider() {
        return agenda;
    }

    @Override
    public void setProvider(Agenda provider) {
        this.agenda = provider;
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(agenda);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

}
