import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlToMarkdownConverter {

    public static void main(String[] args) {
        // HTML 文件路径
        String htmlFilePath = "path/to/your/html/file.html";

        try {
            // 使用 Jsoup 解析 HTML 文件
            Document doc = Jsoup.parse(new File(htmlFilePath), "UTF-8");

            // 获取整个 HTML 文档的 Markdown
            String markdown = convertHtmlToMarkdown(doc);

            // 输出 Markdown
            System.out.println(markdown);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将 Jsoup 的 Document 对象表示的整个 HTML 转换成 Markdown 格式
    private static String convertHtmlToMarkdown(Document doc) {
        StringBuilder markdown = new StringBuilder();

        // 处理 HTML 中的各种元素
        Elements elements = doc.body().children();
        for (Element element : elements) {
            String tagName = element.tagName();

            switch (tagName) {
                case "h1", "h2", "h3", "h4", "h5", "h6" -> {
                    // 处理标题
                    markdown.append(convertHeaderToMarkdown(element)).append("\n\n");
                }
                case "p" -> {
                    // 处理段落
                    markdown.append(convertParagraphToMarkdown(element)).append("\n\n");
                }
                case "ul", "ol" -> {
                    // 处理列表
                    markdown.append(convertListToMarkdown(element)).append("\n\n");
                }
                case "blockquote" -> {
                    // 处理引用块
                    markdown.append(convertBlockquoteToMarkdown(element)).append("\n\n");
                }
                case "pre" -> {
                    // 处理预格式化文本
                    markdown.append(convertPreformattedToMarkdown(element)).append("\n\n");
                }
                case "img" -> {
                    // 处理图片
                    markdown.append(convertImageToMarkdown(element)).append("\n\n");
                }
                case "table" -> {
                    // 处理表格
                    markdown.append(convertTableToMarkdown(element)).append("\n\n");
                }
                default -> {
                    // 处理其他元素，如 div、span 等
                    markdown.append(convertElementToMarkdown(element)).append("\n\n");
                }
            }
        }

        return markdown.toString();
    }

    // 将标题元素转换成 Markdown
    private static String convertHeaderToMarkdown(Element element) {
        int level = Integer.parseInt(element.tagName().substring(1)); // e.g., "h1" -> 1
        return "#".repeat(level) + " " + element.text();
    }

    // 将段落元素转换成 Markdown
    private static String convertParagraphToMarkdown(Element element) {
        return element.text();
    }

    // 将列表元素转换成 Markdown
    private static String convertListToMarkdown(Element element) {
        StringBuilder markdown = new StringBuilder();
        String tagName = element.tagName();

        // 判断是无序列表还是有序列表
        if ("ul".equalsIgnoreCase(tagName)) {
            for (Element li : element.select("li")) {
                markdown.append("- ").append(li.text()).append("\n");
            }
        } else if ("ol".equalsIgnoreCase(tagName)) {
            int index = 1;
            for (Element li : element.select("li")) {
                markdown.append(index++).append(". ").append(li.text()).append("\n");
            }
        }

        return markdown.toString();
    }

    // 将引用块元素转换成 Markdown
    private static String convertBlockquoteToMarkdown(Element element) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("> ");
        markdown.append(element.text().replaceAll("\n", "\n> ")); // 处理换行
        return markdown.toString();
    }

    // 将预格式化文本元素转换成 Markdown
    private static String convertPreformattedToMarkdown(Element element) {
        return "```\n" + element.text() + "\n```";
    }

    // 将图片元素转换成 Markdown
    private static String convertImageToMarkdown(Element element) {
        String altText = element.attr("alt");
        String src = element.attr("src");
        return "!["
                + (altText.isEmpty() ? "Image" : altText)
                + "]("
                + src
                + ")";
    }

    // 将表格元素转换成 Markdown
    private static String convertTableToMarkdown(Element element) {
        StringBuilder markdown = new StringBuilder();

        // 处理表头
        Element thead = element.selectFirst("thead");
        if (thead != null) {
            markdown.append(convertTableSectionToMarkdown(thead)).append("\n");
        }

        // 处理表体
        Element tbody = element.selectFirst("tbody");
        if (tbody != null) {
            markdown.append(convertTableSectionToMarkdown(tbody)).append("\n");
        }

        return markdown.toString();
    }

    // 将表格的表头或表体转换成 Markdown
    private static String convertTableSectionToMarkdown(Element section) {
        StringBuilder markdown = new StringBuilder();

        Elements rows = section.select("tr");
        for (Element row : rows) {
            Elements cells = row.select("td, th");
            for (Element cell : cells) {
                String cellText = cell.text().replaceAll("\\|", "&#124;"); // 替换掉单元格内容中的管道符
                markdown.append("| ").append(cellText).append(" ");
            }
            markdown.append("|\n");
        }

        // 处理表头和内容之间的分隔线
        for (Element row : rows) {
            Elements cells = row.select("td, th");
            for (Element cell : cells) {
                markdown.append("|---");
            }
            markdown.append("|\n");
            break; // 只需要第一行的分隔线
        }

        return markdown.toString();
    }

    // 将普通元素转换成 Markdown（默认情况）
    private static String convertElementToMarkdown(Element element) {
        return element.outerHtml(); // 直接输出原始 HTML 标签内容
    }
}
