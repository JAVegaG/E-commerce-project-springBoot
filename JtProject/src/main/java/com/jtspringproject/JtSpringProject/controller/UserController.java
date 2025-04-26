// Modificar: src/main/java/com/jtspringproject/JtSpringProject/controller/UserController.java
package com.jtspringproject.JtSpringProject.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.OrderService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

// Mantener el resto del código existente...
@Controller
public class UserController {

    // Inyectar el nuevo servicio
    @Autowired
    private OrderService orderService;

    private final userService userService;
    private final productService productService;

    @Autowired
    public UserController(userService userService, productService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/register")
    public String registerUser() {
        return "register";
    }

    @GetMapping("/buy")
    public String buy() {
        return "buy";
    }

    @GetMapping("/login")
    public ModelAndView userlogin(@RequestParam(required = false) String error) {
        ModelAndView mv = new ModelAndView("userLogin");
        if ("true".equals(error)) {
            mv.addObject("msg", "Please enter correct email and password");
        }
        return mv;
    }

    @GetMapping("/")
    public ModelAndView indexPage() {
        ModelAndView mView = new ModelAndView("index");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        mView.addObject("username", username);
        List<Product> products = this.productService.getProducts();

        if (products.isEmpty()) {
            mView.addObject("msg", "No products are available");
        } else {
            mView.addObject("products", products);
        }
        return mView;
    }

    @GetMapping("/user/products")
    public ModelAndView getproduct() {

        ModelAndView mView = new ModelAndView("uproduct");

        List<Product> products = this.productService.getProducts();

        if (products.isEmpty()) {
            mView.addObject("msg", "No products are available");
        } else {
            mView.addObject("products", products);
        }

        return mView;
    }

    @RequestMapping(value = "newuserregister", method = RequestMethod.POST)
    public ModelAndView newUseRegister(@ModelAttribute User user) {
        // Check if username already exists in database
        boolean exists = this.userService.checkUserExists(user.getUsername());

        if (!exists) {
            System.out.println(user.getEmail());
            user.setRole("ROLE_NORMAL");
            this.userService.addUser(user);

            System.out.println("New user created: " + user.getUsername());
            ModelAndView mView = new ModelAndView("userLogin");
            return mView;
        } else {
            System.out.println("New user not created - username taken: " + user.getUsername());
            ModelAndView mView = new ModelAndView("register");
            mView.addObject("msg", user.getUsername() + " is taken. Please choose a different username.");
            return mView;
        }
    }

    @GetMapping("/profileDisplay")
    public String profileDisplay(Model model, HttpServletRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);

        if (user != null) {
            model.addAttribute("userid", user.getId());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("password", user.getPassword());
            model.addAttribute("address", user.getAddress());
        } else {
            model.addAttribute("msg", "User not found");
        }

        return "updateProfile";
    }

    //for Learning purpose of model
    @GetMapping("/test")
    public String Test(Model model) {
        System.out.println("test page");
        model.addAttribute("author", "jay gajera");
        model.addAttribute("id", 40);

        List<String> friends = new ArrayList<>();
        model.addAttribute("f", friends);
        friends.add("xyz");
        friends.add("abc");

        return "test";
    }

    // for learning purpose of model and view ( how data is pass to view)
    @GetMapping("/test2")
    public ModelAndView Test2() {
        System.out.println("test page");
        //create modelandview object
        ModelAndView mv = new ModelAndView();
        mv.addObject("name", "jay gajera 17");
        mv.addObject("id", 40);
        mv.setViewName("test2");

        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(25);
        mv.addObject("marks", list);
        return mv;

    }

    // Mantener las inyecciones y métodos existentes...
    // Añadir nuevo endpoint para checkout
    @PostMapping("/checkout")
    public String checkout(@RequestParam("amount") double amount,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("paymentInfo") String paymentInfo,
            HttpSession session) {

        // Obtener el ID del usuario de la sesión
        String userId = (String) session.getAttribute("userId");

        // Crear la orden usando nuestro servicio
        String orderId = orderService.createOrder(amount, userId, paymentMethod, paymentInfo);

        if (orderId != null) {
            // Redirigir a página de confirmación
            return "redirect:/orderConfirmation?orderId=" + orderId;
        } else {
            // Redirigir a página de error
            return "redirect:/checkoutError";
        }
    }

    // Añadir endpoint para cancelación
    @PostMapping("/cancelOrder")
    public String cancelOrder(@RequestParam("orderId") String orderId,
            @RequestParam("transactionId") String transactionId) {

        boolean canceled = orderService.cancelOrder(orderId, transactionId);

        if (canceled) {
            return "redirect:/orderCancelled";
        } else {
            return "redirect:/cancellationError";
        }
    }
}
