package com.jtspringproject.JtSpringProject.controller;

import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.models.Cart; // Necesario para ver el carrito
import com.jtspringproject.JtSpringProject.models.CartProduct; // Necesario para ver el carrito

// Quitar imports de JDBC directo si ya no se usan en este controlador.
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Para el cálculo del total

import javax.servlet.http.HttpServletRequest; // Se puede quitar si no se usa

import com.jtspringproject.JtSpringProject.services.cartService; // Importar cartService

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; // Importar Authentication
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Usar asterisco es común o importar específicos
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para mensajes flash

import com.jtspringproject.JtSpringProject.services.userService;
import com.jtspringproject.JtSpringProject.services.productService;

// Imports para el Patrón Mediator
import com.jtspringproject.JtSpringProject.patterns.mediator.ICartMediator;
import com.jtspringproject.JtSpringProject.patterns.mediator.ShoppingCartMediator;

@Controller
public class UserController {

    private final userService userService;
    private final productService productService;
    private final cartService cartService; // Inyectar cartService
    private final ICartMediator cartMediator; // El Mediador

    @Autowired
    public UserController(userService userService,
            productService productService,
            cartService cartService) { // Añadir cartService a la inyección
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService; // Asignar cartService

        // Instanciar el Mediador. Si ShoppingCartMediator fuera un @Component,
        // podrías inyectarlo directamente en el constructor también.
        // Para esta implementación simple, lo instanciamos aquí.
        this.cartMediator = new ShoppingCartMediator(this.userService, this.productService, this.cartService);
    }

    @GetMapping("/register")
    public String registerUser() {
        return "register";
    }

    // Este endpoint parece un placeholder, no tiene funcionalidad real.
    // @GetMapping("/buy")
    // public String buy() {
    // return "buy";
    // }
    @GetMapping("/login") // Endpoint para la página de login de usuario
    public ModelAndView userLogin(@RequestParam(required = false) String error, @RequestParam(required = false) String logout) {
        ModelAndView mv = new ModelAndView("userLogin"); // Vista userLogin.html
        if (error != null) {
            mv.addObject("msg", "Email o contraseña incorrectos. Por favor, intente de nuevo.");
        }
        if (logout != null) {
            mv.addObject("logoutMsg", "Ha cerrado sesión exitosamente.");
        }
        return mv;
    }

    @GetMapping("/")
    public ModelAndView indexPage() {
        ModelAndView mView = new ModelAndView("index"); // Vista index.html (página de inicio)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            mView.addObject("username", authentication.getName());
        }
        // Cargar productos para la página de inicio
        List<Product> products = this.productService.getProducts();
        if (products.isEmpty()) {
            mView.addObject("msg", "No hay productos disponibles en este momento.");
        } else {
            mView.addObject("products", products);
        }
        return mView;
    }

    @GetMapping("/user/products")
    public ModelAndView getUserProducts() { // Renombrado para claridad
        ModelAndView mView = new ModelAndView("uproduct"); // Vista uproduct.html (productos para usuario)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            mView.addObject("username", authentication.getName()); // Pasar nombre de usuario a la vista
        }

        List<Product> products = this.productService.getProducts();
        if (products.isEmpty()) {
            mView.addObject("msg", "No hay productos disponibles.");
        } else {
            mView.addObject("products", products);
        }
        return mView;
    }

    @PostMapping("newuserregister") // Usar @PostMapping
    public ModelAndView newUserRegister(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Verificar si el usuario ya existe
        boolean exists = this.userService.checkUserExists(user.getUsername());

        if (!exists) {
            // Validaciones básicas (ej. campos no vacíos) deberían estar aquí o en el frontend/modelo
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()
                    || user.getPassword() == null || user.getPassword().isEmpty()
                    || user.getEmail() == null || user.getEmail().trim().isEmpty()) {

                ModelAndView mView = new ModelAndView("register");
                mView.addObject("msg", "Todos los campos son obligatorios (nombre de usuario, email, contraseña).");
                mView.addObject("user", user); // Devolver el objeto user para rellenar el formulario
                return mView;
            }

            user.setRole("ROLE_NORMAL"); // Establecer rol por defecto
            // Idealmente, la contraseña se hashearía aquí antes de guardarla.
            // user.setPassword(passwordEncoder.encode(user.getPassword()));
            User registeredUser = this.userService.addUser(user);

            if (registeredUser != null && registeredUser.getId() > 0) {
                System.out.println("Nuevo usuario registrado: " + registeredUser.getUsername());
                redirectAttributes.addFlashAttribute("registerSuccessMsg", "¡Registro exitoso! Por favor, inicie sesión.");
                return new ModelAndView("redirect:/login"); // Redirigir al login
            } else {
                ModelAndView mView = new ModelAndView("register");
                mView.addObject("msg", "Error durante el registro. Por favor, intente de nuevo.");
                mView.addObject("user", user);
                return mView;
            }
        } else {
            System.out.println("Intento de registro fallido - nombre de usuario ya existe: " + user.getUsername());
            ModelAndView mView = new ModelAndView("register");
            mView.addObject("msg", "El nombre de usuario '" + user.getUsername() + "' ya está en uso. Por favor, elija otro.");
            mView.addObject("user", user); // Devolver el objeto user para rellenar el formulario
            return mView;
        }
    }

    // Perfil del usuario (actualización) - Similar al de AdminController, pero para el usuario normal
    @GetMapping("/user/profile")
    public String userProfileDisplay(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login"; // Si no está logueado, redirigir al login
        }
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user != null) {
            model.addAttribute("userid", user.getId());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            // NO pasar la contraseña a la vista.
            model.addAttribute("address", user.getAddress());
        } else {
            model.addAttribute("msg", "Usuario no encontrado."); // Esto no debería pasar si está autenticado
        }
        return "updateProfile"; // Reutilizar la vista updateProfile.html, o crear una específica userUpdateProfile.html
    }

    @PostMapping("/user/updateprofile")
    public String updateUserProfileData(@RequestParam("userid") int userid,
            @RequestParam("username") String newUsername, // Permitir cambio de username
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            // @RequestParam(name="newPassword", required=false) String newPassword, // Para cambio de contraseña
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }
        String currentUsername = authentication.getName();
        User user = userService.getUserByUsername(currentUsername);

        if (user == null || user.getId() != userid) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error de autorización o usuario no encontrado.");
            return "redirect:/"; // Redirigir a la página principal
        }

        // Validar si el nuevo nombre de usuario ya existe (si es diferente al actual)
        if (!newUsername.equals(currentUsername) && userService.checkUserExists(newUsername)) {
            redirectAttributes.addFlashAttribute("errorMsg", "El nuevo nombre de usuario '" + newUsername + "' ya está en uso.");
            return "redirect:/user/profile";
        }

        user.setUsername(newUsername);
        user.setEmail(email);
        user.setAddress(address);
        // Lógica para cambio de contraseña (requiere passwordEncoder y validación de contraseña actual)
        // if (newPassword != null && !newPassword.isEmpty()) { user.setPassword(passwordEncoder.encode(newPassword)); }

        User updatedUser = userService.addUser(user); // addUser también actualiza

        if (updatedUser != null) {
            redirectAttributes.addFlashAttribute("successMsg", "Perfil actualizado correctamente.");
            // Si el nombre de usuario cambió, actualizar el contexto de seguridad
            if (!currentUsername.equals(updatedUser.getUsername())) {
                Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUser.getUsername(), user.getPassword(), authentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "No se pudo actualizar el perfil.");
        }
        return "redirect:/user/profile";
    }

    // --- Endpoints para el Carrito usando el Patrón Mediator ---
    /**
     * Endpoint para añadir un producto al carrito del usuario. Utiliza el
     * ShoppingCartMediator para coordinar la operación.
     */
    @PostMapping("/user/cart/add")
    public String addProductToCart(@RequestParam("productId") int productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("loginErrorMsg", "Debe iniciar sesión para añadir productos al carrito.");
            return "redirect:/login"; // Redirigir al login si no está autenticado
        }
        String username = authentication.getName();

        // Delegar la lógica al mediador
        boolean success = cartMediator.addProductToCart(username, productId, quantity);

        if (success) {
            redirectAttributes.addFlashAttribute("cartMsg", "Producto añadido/actualizado en el carrito exitosamente!");
        } else {
            // El mediador o los servicios ya deberían haber logueado el error específico.
            redirectAttributes.addFlashAttribute("cartErrorMsg", "No se pudo añadir el producto al carrito. Verifique stock o datos, o intente más tarde.");
        }
        // Redirigir a la página de donde vino el usuario, o a la lista de productos, o al carrito.
        // Por simplicidad, redirigimos a la lista de productos del usuario.
        return "redirect:/user/products";
    }

    /**
     * Endpoint para mostrar el carrito del usuario.
     */
    @GetMapping("/user/cart")
    public ModelAndView viewUserCart(RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            // No usar redirectAttributes aquí porque es un GET, el mensaje no se mostraría tras la redirección.
            // Mejor redirigir directamente y que la página de login maneje mensajes.
            return new ModelAndView("redirect:/login");
        }
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) { // Esto no debería ocurrir si está autenticado
            return new ModelAndView("redirect:/login?error=userNotFound");
        }

        ModelAndView mv = new ModelAndView("userCart"); // Necesitarás crear la vista userCart.html
        mv.addObject("username", username);

        Cart cart = cartService.getOrCreateCartForUser(user);
        if (cart != null) {
            List<CartProduct> cartItems = cartService.getCartProductsByCartId(cart.getId());
            mv.addObject("cartItems", cartItems);

            double totalAmount = cartItems.stream()
                    .mapToDouble(cp -> cp.getProduct().getPrice() * cp.getQuantity())
                    .sum();
            mv.addObject("totalAmount", totalAmount);
            if (cartItems.isEmpty()) {
                mv.addObject("cartMsg", "Tu carrito está vacío.");
            }
        } else {
            // Esto tampoco debería ocurrir si getOrCreateCartForUser funciona bien.
            mv.addObject("cartMsg", "No se pudo cargar tu carrito. Intenta de nuevo.");
        }
        return mv;
    }

    // Los métodos de prueba /test y /test2 se mantienen como en el original si son necesarios.
    @GetMapping("/test")
    public String Test(Model model) {
        System.out.println("test page");
        model.addAttribute("author", "jay gajera");
        model.addAttribute("id", 40);
        List<String> friends = new ArrayList<String>();
        model.addAttribute("f", friends);
        friends.add("xyz");
        friends.add("abc");
        return "test";
    }

    @GetMapping("/test2")
    public ModelAndView Test2() {
        System.out.println("test page");
        ModelAndView mv = new ModelAndView();
        mv.addObject("name", "jay gajera 17");
        mv.addObject("id", 40);
        mv.setViewName("test2");
        List<Integer> list = new ArrayList<Integer>();
        list.add(10);
        list.add(25);
        mv.addObject("marks", list);
        return mv;
    }
}
