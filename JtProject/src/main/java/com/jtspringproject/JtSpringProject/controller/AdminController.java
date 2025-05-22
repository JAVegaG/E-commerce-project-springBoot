package com.jtspringproject.JtSpringProject.controller;

import java.sql.Connection; // Estos imports de JDBC directo ya no deberían ser necesarios si usas servicios y DAO
import java.sql.DriverManager; // Quitar si no se usa
import java.sql.PreparedStatement; // Quitar si no se usa
import java.sql.ResultSet; // Quitar si no se usa
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para mensajes flash

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.patterns.command.Command;
import com.jtspringproject.JtSpringProject.patterns.command.UpdateStockCommand;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final userService userService;
    private final categoryService categoryService;
    private final productService productService;

    @Autowired
    public AdminController(userService userService, categoryService categoryService, productService productService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        // Obtener el nombre de usuario de SecurityContext si está configurado, sino manejarlo.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("username", "Admin"); // O un valor por defecto
        }
        return "index";
    }

    @GetMapping("login")
    public ModelAndView adminlogin(@RequestParam(required = false) String error) {
        ModelAndView mv = new ModelAndView("adminlogin");
        if ("true".equals(error)) {
            mv.addObject("msg", "Invalid username or password. Please try again.");
        }
        return mv;
    }

    @GetMapping(value = {"/", "Dashboard"})
    public ModelAndView adminHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView mv = new ModelAndView("adminHome");
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            mv.addObject("admin", authentication.getName());
        } else {
            // Redirigir al login si no está autenticado o manejar de otra forma
            return new ModelAndView("redirect:/admin/login");
        }
        return mv;
    }

    @GetMapping("categories")
    public ModelAndView getcategory() {
        ModelAndView mView = new ModelAndView("categories");
        List<Category> categories = this.categoryService.getCategories();
        mView.addObject("categories", categories);
        return mView;
    }

    @PostMapping("/categories")
    public String addCategory(@RequestParam("categoryname") String category_name) {
        // System.out.println(category_name); // Para debug
        Category category = this.categoryService.addCategory(category_name);
        // La validación de si se creó o no debería hacerse en el servicio o comparar el objeto devuelto.
        // if(category.getName().equals(category_name)) { // Esta comprobación puede ser redundante si el servicio garantiza la creación
        //	return "redirect:categories";
        //}else {
        //	return "redirect:categories";
        //}
        return "redirect:/admin/categories"; // Simplificado
    }

    @GetMapping("categories/delete")
    public String removeCategoryDb(@RequestParam("id") int id) {
        this.categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("categories/update") // Debería ser POST o PUT para actualizaciones
    public String updateCategory(@RequestParam("categoryid") int id, @RequestParam("categoryname") String categoryname) {
        // Category category = this.categoryService.updateCategory(id, categoryname); // El retorno puede usarse para verificar
        this.categoryService.updateCategory(id, categoryname);
        return "redirect:/admin/categories";
    }

    @GetMapping("products")
    public ModelAndView getproduct() {
        ModelAndView mView = new ModelAndView("products");
        List<Product> products = this.productService.getProducts();
        if (products.isEmpty()) {
            mView.addObject("msg", "No products are available");
        } else {
            mView.addObject("products", products);
        }
        return mView;
    }

    @GetMapping("products/add")
    public ModelAndView addProduct() {
        ModelAndView mView = new ModelAndView("productsAdd");
        List<Category> categories = this.categoryService.getCategories();
        mView.addObject("categories", categories);
        return mView;
    }

    @RequestMapping(value = "products/add", method = RequestMethod.POST)
    public String addProduct(
            @RequestParam("name") String name,
            @RequestParam("categoryid") int categoryId,
            @RequestParam("price") int price,
            @RequestParam("weight") int weight,
            @RequestParam("quantity") int quantity,
            @RequestParam("description") String description,
            @RequestParam("productImage") String productImage) {

        Category category = this.categoryService.getCategory(categoryId);
        if (category == null) {
            // Manejar error: categoría no encontrada. Quizás redirigir con mensaje.
            return "redirect:/admin/products/add?error=categoryNotFound";
        }
        Product product = new Product();
        // product.setId(categoryId); // El ID del producto es autogenerado, no debería ser el ID de la categoría.
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setPrice(price);
        product.setImage(productImage);
        product.setWeight(weight);
        product.setQuantity(quantity);
        this.productService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("products/update/{id}")
    public ModelAndView updateproduct(@PathVariable("id") int id) {
        ModelAndView mView = new ModelAndView("productsUpdate");
        Product product = this.productService.getProduct(id);
        if (product == null) {
            // Manejar producto no encontrado, quizás redirigir a la lista de productos
            mView.setViewName("redirect:/admin/products");
            mView.addObject("errorMsg", "Producto con ID " + id + " no encontrado.");
            return mView;
        }
        List<Category> categories = this.categoryService.getCategories();
        mView.addObject("categories", categories);
        mView.addObject("product", product);
        return mView;
    }

    /**
     * Actualiza un producto existente. La cantidad se actualiza usando el
     * método directo del servicio por ahora. Para usar Command aquí para la
     * cantidad, se necesitaría calcular el 'quantityChange'.
     */
    @RequestMapping(value = "products/update/{id}", method = RequestMethod.POST)
    public String updateProduct(
            @PathVariable("id") int id,
            @RequestParam("name") String name,
            @RequestParam("categoryid") int categoryId,
            @RequestParam("price") int price,
            @RequestParam("weight") int weight,
            @RequestParam("quantity") int quantity, // Nueva cantidad total
            @RequestParam("description") String description,
            @RequestParam("productImage") String productImage,
            RedirectAttributes redirectAttributes) {

        Product product = this.productService.getProduct(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Producto no encontrado para actualizar.");
            return "redirect:/admin/products";
        }
        Category category = this.categoryService.getCategory(categoryId);
        if (category == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Categoría no encontrada.");
            return "redirect:/admin/products/update/" + id;
        }

        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setWeight(weight);
        product.setQuantity(quantity); // Se actualiza la cantidad directamente
        product.setDescription(description);
        product.setImage(productImage);

        this.productService.updateProduct(id, product); // El id como parámetro es para tu método de servicio existente
        redirectAttributes.addFlashAttribute("successMsg", "Producto actualizado correctamente.");
        return "redirect:/admin/products";
    }

    @GetMapping("products/delete")
    public String removeProduct(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        boolean deleted = this.productService.deleteProduct(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMsg", "Producto eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "No se pudo eliminar el producto.");
        }
        return "redirect:/admin/products";
    }

    // Este método parece redundante o un placeholder, considerar su propósito.
    // @PostMapping("products")
    // public String postproduct() {
    //	return "redirect:/admin/categories";
    // }
    @GetMapping("customers")
    public ModelAndView getCustomerDetail() {
        ModelAndView mView = new ModelAndView("displayCustomers");
        List<User> users = this.userService.getUsers();
        mView.addObject("customers", users);
        return mView;
    }

    // Los métodos profileDisplay y updateUserProfile usan JDBC directo.
    // Se recomienda refactorizarlos para usar userService y userDao consistentemente.
    // Por ahora, los mantenemos como están para enfocarnos en el patrón Command.
    @GetMapping("profileDisplay")
    public String profileDisplay(Model model) {
        String displayusername, displaypassword, displayemail, displayaddress;
        try {
            // TODO: Refactorizar para usar userService
            Class.forName("com.mysql.cj.jdbc.Driver"); // Usar el driver más reciente si es MySQL 8+
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava", "root", "");
            PreparedStatement stmt = con.prepareStatement("select * from users where username = ?");
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            stmt.setString(1, username);
            ResultSet rst = stmt.executeQuery();
            if (rst.next()) {
                int userid = rst.getInt("uid"); // Asumiendo que la columna se llama 'uid'
                displayusername = rst.getString("username");
                displayemail = rst.getString("email");
                displaypassword = rst.getString("password"); // Considerar no mostrar la contraseña
                displayaddress = rst.getString("address");
                model.addAttribute("userid", userid);
                model.addAttribute("username", displayusername);
                model.addAttribute("email", displayemail);
                // model.addAttribute("password",displaypassword); // No es buena práctica pasar el hash o texto plano a la vista
                model.addAttribute("address", displayaddress);
            }
            rst.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.err.println("Exception en profileDisplay:" + e);
            model.addAttribute("errorMsg", "Error al cargar el perfil.");
        }
        return "updateProfile";
    }

    @RequestMapping(value = "updateuser", method = RequestMethod.POST)
    public String updateUserProfile(@RequestParam("userid") int userid,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password, // Recibir la nueva contraseña
            @RequestParam("address") String address,
            RedirectAttributes redirectAttributes) {
        try {
            // TODO: Refactorizar para usar userService.
            // El userService debería manejar el hash de la contraseña si se cambia.
            // Esta implementación actualiza la contraseña en texto plano si se proporciona.
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava", "root", "");
            // Sentencia para actualizar, incluyendo la contraseña si se desea cambiar
            // Es mejor separar el cambio de contraseña en otra funcionalidad.
            PreparedStatement pst = con.prepareStatement("update users set username= ?,email = ?, address= ? where uid = ?;");
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, address);
            pst.setInt(4, userid);
            int i = pst.executeUpdate();

            // Si la contraseña se va a cambiar, debería ser un campo opcional y hashearse.
            // Ejemplo de cómo actualizar la contraseña si se proporciona una nueva (no recomendado así directo):
            // if (password != null && !password.isEmpty()) {
            // PreparedStatement pstPass = con.prepareStatement("update users set password = ? where uid = ?;");
            // pstPass.setString(1, password); // ¡Debería ser hasheada!
            // pstPass.setInt(2, userid);
            // pstPass.executeUpdate();
            // pstPass.close();
            // }
            pst.close();
            con.close();

            // Actualizar el principal de seguridad si el nombre de usuario cambió
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (!currentAuth.getName().equals(username)) {
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                        username,
                        currentAuth.getCredentials(), // Mantener las credenciales (contraseña hasheada) o null si no se conoce
                        currentAuth.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }
            redirectAttributes.addFlashAttribute("successMsg", "Perfil actualizado correctamente.");
        } catch (Exception e) {
            System.err.println("Exception en updateUserProfile:" + e);
            redirectAttributes.addFlashAttribute("errorMsg", "Error al actualizar el perfil.");
        }
        return "redirect:/admin/index"; // O a la página de perfil
    }

    // --- Métodos para el Patrón Command ---
    /**
     * Método genérico para ejecutar cualquier comando.
     *
     * @param command El comando a ejecutar.
     */
    private void executeCommand(Command command) {
        command.execute();
    }

    /**
     * Endpoint para ajustar el stock de un producto usando el Patrón Command.
     *
     * @param productId El ID del producto.
     * @param quantityChange La cantidad a sumar o restar del stock.
     * @param redirectAttributes Para enviar mensajes de feedback a la vista.
     * @return Redirección a la lista de productos.
     */
    @PostMapping("/products/adjustStock")
    public String adjustProductStock(@RequestParam("productId") int productId,
            @RequestParam("quantityChange") int quantityChange,
            RedirectAttributes redirectAttributes) {

        // Crear el comando con el servicio receptor y los parámetros
        Command updateStockCmd = new UpdateStockCommand(this.productService, productId, quantityChange);

        try {
            executeCommand(updateStockCmd); // Ejecutar el comando
            redirectAttributes.addFlashAttribute("successMsg", "Stock del producto ID " + productId + " ajustado en " + quantityChange + " unidades.");
        } catch (Exception e) {
            // Si updateProductQuantity lanza una excepción específica, podrías capturarla aquí.
            System.err.println("Error al ejecutar UpdateStockCommand: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMsg", "Error al ajustar stock: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }
}
