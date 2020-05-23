package tv.skimo.meeting.lib;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public enum ThymeLeafConfig {
    INSTANCE;
    private TemplateEngine templateEngine;
     
    private ThymeLeafConfig(){
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix(getTemplatePath());
        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix("src/main/resources/templates/");
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }
     
    private String getTemplatePath(){
        return ThymeLeafConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "templates/";
    }
     
    public static TemplateEngine getTemplateEngine(){
        return INSTANCE.templateEngine;
    }
}
