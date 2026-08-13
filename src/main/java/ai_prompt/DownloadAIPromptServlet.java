package ai_prompt;

import algorithm.Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/download-ai-prompt")
public class DownloadAIPromptServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (Main.state == null) {

            response.setContentType("text/plain");

            response.getWriter().println(
                    "Routine has not been generated yet.");

            return;
        }

        String prompt = AIPromptGenerator.generate(Main.state);

        String fileName =
                "AI_Prompt_"
                        + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                        + ".txt";

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + fileName + "\"");

        PrintWriter out = response.getWriter();

        out.print(prompt);

        out.flush();
    }
}