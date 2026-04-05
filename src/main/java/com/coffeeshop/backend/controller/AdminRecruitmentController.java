package com.coffeeshop.backend.controller;
import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/recruitment")
@RequiredArgsConstructor
public class AdminRecruitmentController {
    private final RecruitmentService recruitmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("jobs", recruitmentService.findAll());
        return "admin/recruitment";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("job", new JobPosition());
        model.addAttribute("branches", recruitmentService.findAllBranches());
        model.addAttribute("jobTypes", JobPosition.JobType.values());
        return "admin/job-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("job", recruitmentService.findById(id).orElseThrow());
        model.addAttribute("branches", recruitmentService.findAllBranches());
        model.addAttribute("jobTypes", JobPosition.JobType.values());
        return "admin/job-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute JobPosition job) {
        recruitmentService.save(job);
        return "redirect:/admin/recruitment";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        recruitmentService.deleteById(id);
        return "redirect:/admin/recruitment";
    }

    @GetMapping("/{id}/applications")
    public String applications(@PathVariable Long id, Model model) {
        model.addAttribute("job", recruitmentService.findById(id).orElseThrow());
        model.addAttribute("applications", recruitmentService.findApplicationsByJob(id));
        model.addAttribute("statuses", JobApplication.Status.values());
        return "admin/applications";
    }

    @PostMapping("/applications/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               @RequestParam(required = false) String note) {
        recruitmentService.findApplicationById(id).ifPresent(app -> {
            app.setStatus(JobApplication.Status.valueOf(status));
            app.setAdminNote(note);
            app.setReviewedAt(LocalDateTime.now());
            recruitmentService.saveApplication(app);
        });
        return "redirect:/admin/recruitment";
    }
}