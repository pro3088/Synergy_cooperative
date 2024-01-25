package com.synergy.synergy_cooperative.docs;

import javax.validation.constraints.NotNull;

public class DocsDTO {

    private String id;

    @NotNull
    private String image;

    @NotNull
    private String text;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}
