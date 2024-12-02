package com.example.checkin.models;

import java.util.ArrayList;

public class AllForm {
    public AllForm(FormApprove formApprove, Form forms) {
        this.formApprove = formApprove;
        this.forms = forms;
    }

    public FormApprove getFormApprove() {
        return formApprove;
    }

    public Form getForms() {
        return forms;
    }

    public void setFormApprove(FormApprove formApprove) {
        this.formApprove = formApprove;
    }

    public void setForms(Form forms) {
        this.forms = forms;
    }

    private FormApprove formApprove;

    private Form forms;
}
