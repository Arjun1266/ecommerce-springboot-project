package com.example.demo.controller;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Product;
import com.example.demo.service.CartService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    // ------------------------------------------
    // ADD PRODUCT TO CART
    // ------------------------------------------
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        cartService.addProductToCart(productId, username);
        return "redirect:/home?added=true";
    }

    // ------------------------------------------
    // VIEW CART
    // ------------------------------------------
    @GetMapping("/cart/view")
    public String viewCart(Model model, HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        List<Cart> cartItems = cartService.getUserCart(username);
        model.addAttribute("cartItems", cartItems);

        double total = cartItems.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
        model.addAttribute("total", total);

        return "cart";
    }

    // ------------------------------------------
    // INCREASE QUANTITY
    // ------------------------------------------
    @PostMapping("/cart/increase")
    public String increaseQty(@RequestParam("cartId") Long cartId) {
        cartService.increaseQuantity(cartId);
        return "redirect:/cart/view";
    }

    // ------------------------------------------
    // DECREASE QUANTITY
    // ------------------------------------------
    @PostMapping("/cart/decrease")
    public String decreaseQty(@RequestParam("cartId") Long cartId) {
        cartService.decreaseQuantity(cartId);
        return "redirect:/cart/view";
    }

    // ------------------------------------------
    // REMOVE ITEM
    // ------------------------------------------
    @PostMapping("/cart/delete")
    public String deleteItem(@RequestParam("cartId") Long cartId) {
        cartService.removeItem(cartId);
        return "redirect:/cart/view";
    }

    // ------------------------------------------
    // DOWNLOAD FULL CART RECEIPT PDF
    // ------------------------------------------
    @GetMapping("/cart/receipt")
    public void generateReceipt(HttpServletResponse response, HttpSession session) throws Exception {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("/login");
            return;
        }

        List<Cart> cartItems = cartService.getUserCart(username);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=receipt.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Paragraph title = new Paragraph("E-Commerce Store - Purchase Receipt\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("Customer: " + username));
        document.add(new Paragraph("Date: " + new java.util.Date()));
        document.add(new Paragraph("\n---------------------------------------------\n"));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Product");
        table.addCell("Price");
        table.addCell("Qty");
        table.addCell("Total");

        double grandTotal = 0;

        for (Cart item : cartItems) {
            table.addCell(item.getProduct().getName());
            table.addCell("₹" + item.getProduct().getPrice());
            table.addCell(String.valueOf(item.getQuantity()));

            double total = item.getProduct().getPrice() * item.getQuantity();
            table.addCell("₹" + total);

            grandTotal += total;
        }

        document.add(table);

        Paragraph totalPara = new Paragraph("\nGrand Total: ₹" + grandTotal,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        totalPara.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalPara);

        document.close();
    }

    // ------------------------------------------
    // BUY SINGLE PRODUCT -> PDF
    // ------------------------------------------
    @GetMapping("/buy/{productId}")
    public void buySingleProduct(@PathVariable("productId") Long productId,
                                 HttpSession session,
                                 HttpServletResponse response) throws Exception {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("/login");
            return;
        }

        Product product = cartService.getProductById(productId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=product_receipt.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Paragraph title = new Paragraph("Purchase Receipt - Single Product\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("Customer: " + username));
        document.add(new Paragraph("Date: " + new java.util.Date()));
        document.add(new Paragraph("\n---------------------------------------------\n"));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Product");
        table.addCell("Price");
        table.addCell("Qty");
        table.addCell("Total");

        int qty = 1;
        double total = product.getPrice() * qty;

        table.addCell(product.getName());
        table.addCell("₹" + product.getPrice());
        table.addCell(String.valueOf(qty));
        table.addCell("₹" + total);

        document.add(table);

        Paragraph totalPara = new Paragraph("\nGrand Total: ₹" + total,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        totalPara.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalPara);

        document.close();
    }
}
