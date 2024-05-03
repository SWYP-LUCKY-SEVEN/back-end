//package com.example.swip.controller;
//
//@RestController
//@RequiredArgsConstructor
//public class HelloController {
//    private final S3Service s3Service;
//    @GetMapping("/")
//    public String greeting(){
//        return "Hello, World";
//    }
//
//    @GetMapping("/secured")
//    public String secured(@AuthenticationPrincipal UserPrincipal principal) {
//        return "If you see this, then you're logged in as user " + principal.getEmail()
//                + " User ID: " + principal.getUserId();
//    }
//}