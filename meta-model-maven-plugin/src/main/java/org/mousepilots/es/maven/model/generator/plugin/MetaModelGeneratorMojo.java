package org.mousepilots.es.maven.model.generator.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.metamodel.StaticMetamodel;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.mousepilots.es.maven.model.generator.controller.TypeGenerator;
import org.mousepilots.es.maven.model.generator.model.Descriptor;
import org.mousepilots.es.maven.model.generator.model.type.TypeDescriptor;
import org.reflections.Reflections;

@Mojo(name = "generate",
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        executionStrategy = "once-per-session"
)
public class MetaModelGeneratorMojo extends AbstractMojo {

    /**
     * used to prevent multiple executions within the same maven session
     */
    private static boolean executed = false;

    /**
     * The fully qualified name of the package in which sources are generated by the plugin. E.g. {@code org.my.package}. The plugin must have this and all of its sub-packages to itself
     */
    @Parameter(required = true)
    private String packageName;

    /**
     * The maven project that is executing this plugin during its compile phase.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * The project folder in which generated sources are put. The plugin should have this folder and all sub-folders to itself.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/es", readonly = true)
    private File generatedSourceDir;
    
    /**
     * Allows you to specify a list of properties which do not comply with the Java Beans Standard. The getters and/or setters
     * of such properties cannot be found automatically by the plugin.
     */
    @Parameter
    private List<PropertyDefinition> nonJavaBeansCompliantProperties;

    /**
     * Contains an in-memory representation of the reflections xml report of the domain project
     */
    private Reflections reflections;

    /**
     * Contains the name and version of this plugin. Which will be inserted in the generated classes.
     */
    static final String ES_NAME_AND_VERSION = getEsNameAndVersion();

    /**
     * Contains the current date. Which will be inserted in the generated classes.
     */
    static final Date currentDate = new Date();
    
    /**
     * Load the name of the plugin and its version from the properties file. So it can be used to annotate the generated meta model classes.
     *
     * @return A {@link String} containing the name and version of the plugin.
     */
    private static String getEsNameAndVersion() {
        final Properties properties = new Properties();
        final StringBuilder sb = new StringBuilder();
        try {
            InputStream resourceAsStream = Descriptor.class.getResourceAsStream(
                    "/project.properties");
            if (resourceAsStream != null) {
                properties.load(resourceAsStream);
                sb.append(properties.getProperty("artifactId", "Unknown"));
                sb.append("_");
                sb.append(properties.getProperty("version", "unknown version"));
            } else {
                sb.append("Unknown_unknown version");
            }
        } catch (IOException ex) {
            Logger.getLogger(Descriptor.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        return sb.toString();
    }
    

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(nonJavaBeansCompliantProperties!=null){
            nonJavaBeansCompliantProperties.forEach(p -> getLog().info(p.toString()));
        }
        if (executed) {
            return;
        } else {
            executed = true;
        }
        getLog().info("Starting meta model generation.");
        FileUtils.initGeneratedSourcesDir(generatedSourceDir, project);
        getLog().info("Initialized generated source-folder " + this.generatedSourceDir.getAbsolutePath());
        reflections = new Reflections((String) null);
        final Set<Class<?>> jpaMetaModelClasses = reflections.getTypesAnnotatedWith(StaticMetamodel.class);
        if (jpaMetaModelClasses.isEmpty()) {
            //No meta model classes found.
            getLog().info("No meta model classes were found, stopping execution.");
            return;
        }
        getLog().info("Found " + jpaMetaModelClasses.size() + " meta model classes.");
        SortedSet<TypeDescriptor> generatedTypes = null;
        try {
            final TypeGenerator generator = new TypeGenerator(getLog());
            generatedTypes = generator.generate(jpaMetaModelClasses);
        } catch (IllegalStateException ex) {
            getLog().error("Generator failed to generate types.", ex);
            throw new MojoFailureException("Generator failed to generate types", ex);
        }
        if (generatedTypes == null) {
            throw new MojoFailureException("Generator failed to generate types");
        }
        //printGeneratedTypes(generatedTypes);
        final MetaModelWriter writer = new MetaModelWriter(generatedSourceDir, getLog(), packageName);
        writer.writeHasValueImpls();
        getLog().info("Wrote HasValue implementations");
        writer.writeAbstractTypeImpls();
        getLog().info("Wrote the abstract types and attributes");
        writer.writeProxyImpls();
        getLog().info("Wrote proxies");
        writer.writeMetaModel();
        getLog().info("Successfully completed meta model generation");
    }

}
