package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.JobApplication;
import com.coffeeshop.backend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tuyen-dung")
@RequiredArgsConstructor
public class RecruitmentPublicController {

    private final RecruitmentService recruitmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("jobs", recruitmentService.findAll().stream()
                .filter(j -> j.getStatus().name().equals("OPEN")).toList());
        model.addAttribute("branches", recruitmentService.findAllBranches());
        return "user/recruitment";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("job", recruitmentService.findById(id).orElseThrow());
        return "user/recruitment";
    }

    @PostMapping("/{id}/apply")
    public String apply(@PathVariable Long id,
                        @RequestParam String applicantName,
                        @RequestParam String applicantEmail,
                        @RequestParam(required = false) String applicantPhone,
                        @RequestParam(required = false) String coverLetter) {
        recruitmentService.findById(id).ifPresent(job -> {
            JobApplication app = JobApplication.builder()
                    .job(job)
                    .applicantName(applicantName)
                    .applicantEmail(applicantEmail)
                    .applicantPhone(applicantPhone)
                    .coverLetter(coverLetter)
                    .status(JobApplication.Status.PENDING)
                    .build();
            recruitmentService.saveApplication(app);
        });
        return "redirect:/tuyen-dung?applied=true";
    }
}