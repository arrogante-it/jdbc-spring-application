package com.example.controller;

import com.example.dao.AccountDao;
import com.example.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@org.springframework.stereotype.Controller
@RequestMapping(value = "/people")
public class AccountController {

    private final AccountDao accountDao;

    @Autowired
    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GetMapping()
    public String showPeople(Model model) {
        model.addAttribute("people", accountDao.findAll());
        return "people/showPeople";
    }

    @GetMapping(value = "/{id}")
    public String showPerson(@PathVariable(value = "id") Long id, Model model) {
        model.addAttribute("person", accountDao.findOne(id));
        return "people/showPerson";
    }

    @GetMapping(value = "/createPersonPage")
    public String createPersonPage(@ModelAttribute(value = "person") Account account) {
        return "people/createPersonPage";
    }

    @PatchMapping
    public String create(@ModelAttribute(value = "person") Account account) {
        accountDao.save(account);
        return "redirect:/people";
    }

    @GetMapping(value = "/{id}/edit")
    public String edit(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("person", accountDao.findOne(id));
        return "people/edit";
    }

//    @PatchMapping(value = "/{id}")
//    public String update(@ModelAttribute(value = "person") Account newAccount, @PathVariable(value = "id") Long id) {
//        accountDao.update(id, newAccount);
//        return "redirect:/people";
//    }

//    @DeleteMapping(value = "/{id}")
//    public String delete(@PathVariable(value = "id") int id) {
//        personDAO.delete(id);
//        return "redirect:/people";
//    }
}