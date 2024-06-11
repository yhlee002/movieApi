package com.portfolio.demo.project;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

public class ArchitectureTest {

    JavaClasses javaClasses;

    @BeforeEach
    public void beforeEach() {
        javaClasses =
                new ClassFileImporter()
                        .withImportOption(new ImportOption.DoNotIncludeTests())
                        .importPackages("com.portfolio.demo.project");
    }

    @Test
    @DisplayName("controller 패키지 속 클래스들은 'Api'로 끝나야 한다.")
    public void controllerTest1() {
        ArchRule rule = classes()
                .that()
                .resideInAnyPackage("..controller")
                .should()
                .haveSimpleNameEndingWith("Api");

        ArchRule annotationRule = classes()
                .that()
                .resideInAnyPackage("..controller")
                .should()
                .beAnnotatedWith(Controller.class)
                .orShould()
                .beAnnotatedWith(RestController.class);

        rule.check(javaClasses);
        annotationRule.check(javaClasses);
    }

    @Test
    @DisplayName("controller 패키지 속 클래스들은 @Controller 또는 @RestController 어노테이션을 가진다.")
    public void controllerTest2() {
        ArchRule annotationRule = classes()
                .that()
                .resideInAnyPackage("..controller")
                .should()
                .beAnnotatedWith(Controller.class)
                .orShould()
                .beAnnotatedWith(RestController.class);

        annotationRule.check(javaClasses);
    }

    @Test
    @DisplayName("service 패키지 속 클래스들은 Service로 끝나야 하고, @Service 어노테이션이 붙어야 한다.")
    public void serviceTest() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..service..")
                        .should()
                        .haveSimpleNameEndingWith("Service")
                        .andShould()
                        .beAnnotatedWith(Service.class);

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("repository 패키지 속 클래스들은 Repository로 끝나야 하고, 인터페이스여야 한다.")
    public void repositoryTest() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..repository")
                        .should()
                        .haveSimpleNameEndingWith("Repository")
                        .andShould()
                        .beInterfaces();

        rule.check(javaClasses);
    }
/*
    @Test
    @DisplayName("entity 패키지 안에 있는 클래스들은 @Table, @Entity 어노테이션이 붙어야 한다.")
    public void entityTest() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..entity..")
                        .should()
                        .beAnnotatedWith(Table.class)
                        .andShould()
                        .beAnnotatedWith(Entity.class);
        // @Getter, @Setter, @ToString, @NoArgConstructor : Lombok

        rule.check(javaClasses);
    }
*/
    @Test
    @DisplayName("config 패키지 안에 있는 클래스는 Config로 끝나야 하고, @Configuration 어노테이션이 붙어야 한다.")
    public void configTest() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..config")
                        .should()
                        .haveSimpleNameEndingWith("Config")
                        .andShould()
                        .beAnnotatedWith(Configuration.class);

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("controller는 Service를 사용할 수 있다.")
    public void controllerDependencyTest() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..controller")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("..service..");

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("controller는 의존되지 않는다.")
    public void controllerDependencyTest2() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..controller")
                        .should()
                        .onlyHaveDependentClassesThat()
                        .resideInAnyPackage("..controller");

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("controller는 entity를 사용할 수 없다.")
    public void controllerDependencyTest3() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAnyPackage("..controller")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("..model..");

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("service는 controller를 의존하면 안된다.")
    public void serviceDependencyTest() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAnyPackage("..service..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("..controller");

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("entity은 오직 service와 repository, dto 생성에만 의존된다.")
    public void modelDependencyTest() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..entity..")
                        .and()
                        .areNotEnums()
                        .should()
                        .onlyHaveDependentClassesThat()
                        .resideInAnyPackage("..repository..", "..service..", "..entity..", "..dto..",
                                "..security..");

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("entity는 아무것도 의존하지 않는다.")
    public void modelDependencyTest2() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..entity..")
                        .should()
                        .onlyDependOnClassesThat()
                        .resideInAnyPackage("..entity..", "java..", "jakarta..",
                                "..jpa.domain..", "..org.hibernate..", "..security..",
                                "..springframework.."); // DateTimeFormatter, LastModifiedDate

        rule.check(javaClasses);
    }
}
