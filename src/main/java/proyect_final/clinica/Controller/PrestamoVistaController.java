// package com.clinica_odontologica.V1.Controller;

// import com.clinica_odontologica.V1.Model.Entity.PrestamoActual;
// import com.clinica_odontologica.V1.Service.PrestamoActualService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @Controller
// @RequestMapping("/prestamos")
// public class PrestamoVistaController {

//     @Autowired
//     private PrestamoActualService prestamoActualService;

//     @GetMapping
//     public String listarPrestamos(
//             @RequestParam(required = false) Long estudiante,
//             @RequestParam(required = false) Long archivo,
//             @RequestParam(required = false) String estado,
//             Model model) {
        
//         List<PrestamoActual> prestamos;
        
//         // Aplicar filtros si existen
//         if (estudiante != null) {
//             prestamos = prestamoActualService.buscarPorIdEstudiante(estudiante);
//         } else if (archivo != null) {
//             prestamos = prestamoActualService.buscarPorIdArchivo(archivo);
//         } else if (estado != null && !estado.isEmpty()) {
//             prestamos = prestamoActualService.buscarPorEstado(estado);
//         } else {
//             prestamos = prestamoActualService.obtenerTodos();
//         }
        
//         // Calcular estadísticas
//         long totalPrestamos = prestamos.size();
//         long prestamosActivos = prestamos.stream()
//                 .filter(p -> "ACTIVO".equals(p.getEstadoPrestamo()))
//                 .count();
//         long prestamosVencidos = prestamos.stream()
//                 .filter(p -> "VENCIDO".equals(p.getEstadoPrestamo()))
//                 .count();
//         long prestamosDevueltos = prestamos.stream()
//                 .filter(p -> "DEVUELTO".equals(p.getEstadoPrestamo()))
//                 .count();
        
//         model.addAttribute("prestamos", prestamos);
//         model.addAttribute("totalPrestamos", totalPrestamos);
//         model.addAttribute("prestamosActivos", prestamosActivos);
//         model.addAttribute("prestamosVencidos", prestamosVencidos);
//         model.addAttribute("prestamosDevueltos", prestamosDevueltos);
        
//         return "prestamos/lista";
//     }
// }