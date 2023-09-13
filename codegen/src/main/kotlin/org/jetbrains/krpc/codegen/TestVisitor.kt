package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*

class TestVisitor(private val logger: KSPLogger) : KSVisitor<Unit, Unit> {
    override fun visitAnnotated(annotated: KSAnnotated, data: Unit) {
        logger.warn("received annotated")
        this.visitNode(annotated, data)
    }

    override fun visitAnnotation(annotation: KSAnnotation, data: Unit) {
        logger.warn("received Annotation")
        this.visitNode(annotation, data)
    }

    override fun visitCallableReference(reference: KSCallableReference, data: Unit) {
        logger.warn("received CallableReference")
        this.visitReferenceElement(reference, data)
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        logger.warn("received ClassDeclaration, ${classDeclaration.qualifiedName?.asString()}")
        this.visitDeclaration(classDeclaration, data)
        this.visitDeclarationContainer(classDeclaration, data)
    }

    override fun visitClassifierReference(reference: KSClassifierReference, data: Unit) {
        logger.warn("received ClassifierReference")
        this.visitReferenceElement(reference, data)
    }

    override fun visitDeclaration(declaration: KSDeclaration, data: Unit) {
        logger.warn("received declaration")
        this.visitAnnotated(declaration, data)
        this.visitModifierListOwner(declaration, data)
    }

    override fun visitDeclarationContainer(declarationContainer: KSDeclarationContainer, data: Unit) {
        logger.warn("received declarationContainer")
        this.visitNode(declarationContainer, data)
    }

    override fun visitDefNonNullReference(reference: KSDefNonNullReference, data: Unit) {
        logger.warn("received def non null annotated")
        this.visitReferenceElement(reference, data)
    }

    override fun visitDynamicReference(reference: KSDynamicReference, data: Unit) {
        logger.warn("received dynamic reference")
        this.visitReferenceElement(reference, data)
    }

    override fun visitFile(file: KSFile, data: Unit) {
        logger.warn("received file")
        this.visitAnnotated(file, data)
        this.visitDeclarationContainer(file, data)
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        logger.warn("received function")
        this.visitDeclaration(function, data)
        this.visitDeclarationContainer(function, data)
    }

    override fun visitModifierListOwner(modifierListOwner: KSModifierListOwner, data: Unit) {
        logger.warn("received modifierListOwner")
        this.visitNode(modifierListOwner, data)
    }

    override fun visitNode(node: KSNode, data: Unit) {
        logger.warn("received node")
    }

    override fun visitParenthesizedReference(reference: KSParenthesizedReference, data: Unit) {
        logger.warn("received reference")
        this.visitReferenceElement(reference, data)
    }

    override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: Unit) {
        logger.warn("received accessor")
        this.visitModifierListOwner(accessor, data)
        this.visitAnnotated(accessor, data)
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        logger.warn("received property")
        this.visitDeclaration(property, data)
    }

    override fun visitPropertyGetter(getter: KSPropertyGetter, data: Unit) {
        logger.warn("received getter")
        this.visitPropertyAccessor(getter, data)
    }

    override fun visitPropertySetter(setter: KSPropertySetter, data: Unit) {
        logger.warn("received setter")
        this.visitPropertyAccessor(setter, data)
    }

    override fun visitReferenceElement(element: KSReferenceElement, data: Unit) {
        logger.warn("received element")
        this.visitNode(element, data)
    }

    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: Unit) {
        logger.warn("received typeAlias")
        this.visitDeclaration(typeAlias, data)
    }

    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
        logger.warn("received typeArgument")
        this.visitAnnotated(typeArgument, data)
    }

    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: Unit) {
        logger.warn("received typeParameter")
        this.visitDeclaration(typeParameter, data)
    }

    override fun visitTypeReference(typeReference: KSTypeReference, data: Unit) {
        logger.warn("received typeReference")
        this.visitAnnotated(typeReference, data)
        this.visitModifierListOwner(typeReference, data)
    }

    override fun visitValueArgument(valueArgument: KSValueArgument, data: Unit) {
        logger.warn("received valueArgument")
        this.visitAnnotated(valueArgument, data)
    }

    override fun visitValueParameter(valueParameter: KSValueParameter, data: Unit) {
        logger.warn("received valueParameter")
        this.visitAnnotated(valueParameter, data)
    }

}