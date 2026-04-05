package com.coffeeshop.backend.controller;
import com.coffeeshop.backend.entity.Branch;
import com.coffeeshop.backend.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/branches")
@RequiredArgsConstructor
public class AdminBranchController {
    private final BranchService branchService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("branches", branchService.findAll());
        return "admin/branches";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("branch", new Branch());
        return "admin/branch-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("branch", branchService.findById(id).orElseThrow());
        return "admin/branch-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Branch branch) {
        branchService.save(branch);
        return "redirect:/admin/branches";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        branchService.deleteById(id);
        return "redirect:/admin/branches";
    }
}