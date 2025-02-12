package org.simonegiusso.app;


import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackages;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.type;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

@SuppressWarnings("JUnitTestCaseWithNoTests")
@AnalyzeClasses(importOptions = DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    ArchRule only_service_annotation_from_spring_dependency_is_allowed = classes()
        .should().onlyDependOnClassesThat(
            resideOutsideOfPackages("org.springframework..").or(type(Service.class))
        ).because("The domain should not depend on a technology, but using Spring annotations simplifies dependency injection.");

}
