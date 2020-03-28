/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.ir.declarations.impl

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.carriers.ClassCarrier
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.transform
import org.jetbrains.kotlin.ir.util.mapOptimized
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyExternal
import java.util.*
import kotlin.collections.ArrayList

class IrClassImpl(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    override val symbol: IrClassSymbol,
    override val name: Name = symbol.trueDescriptor.name,
    override val kind: ClassKind = symbol.trueDescriptor.kind,
    visibility: Visibility = symbol.trueDescriptor.visibility,
    modality: Modality = symbol.trueDescriptor.modality,
    override val isCompanion: Boolean = symbol.trueDescriptor.isCompanionObject,
    override val isInner: Boolean = symbol.trueDescriptor.isInner,
    override val isData: Boolean = symbol.trueDescriptor.isData,
    override val isExternal: Boolean = symbol.trueDescriptor.isEffectivelyExternal(),
    override val isInline: Boolean = symbol.trueDescriptor.isInline,
    override val isExpect: Boolean = symbol.trueDescriptor.isExpect,
    override val isFun: Boolean = symbol.trueDescriptor.isFun
) :
    IrDeclarationBase<ClassCarrier>(startOffset, endOffset, origin),
    IrClass,
    ClassCarrier {

    constructor(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrClassSymbol,
        modality: Modality = symbol.trueDescriptor.modality,
        visibility: Visibility = symbol.trueDescriptor.visibility
    ) :
            this(
                startOffset, endOffset, origin, symbol,
                symbol.trueDescriptor.name, symbol.trueDescriptor.kind,
                visibility,
                modality = modality,
                isCompanion = symbol.trueDescriptor.isCompanionObject,
                isInner = symbol.trueDescriptor.isInner,
                isData = symbol.trueDescriptor.isData,
                isExternal = symbol.trueDescriptor.isEffectivelyExternal(),
                isInline = symbol.trueDescriptor.isInline,
                isExpect = symbol.trueDescriptor.isExpect,
                isFun = symbol.trueDescriptor.isFun
            )

    init {
        symbol.bind(this)
    }

    override val descriptor: ClassDescriptor get() = symbol.descriptor

    override var visibilityField: Visibility = visibility

    override var visibility: Visibility
        get() = getCarrier().visibilityField
        set(v) {
            if (visibility !== v) {
                setCarrier().visibilityField = v
            }
        }

    override var thisReceiverField: IrValueParameter? = null

    override var thisReceiver: IrValueParameter?
        get() = getCarrier().thisReceiverField
        set(v) {
            if (thisReceiver !== v) {
                setCarrier().thisReceiverField = v
            }
        }

    private var initialDeclarations: MutableList<IrDeclaration>? = null

    override val declarations: MutableList<IrDeclaration> = ArrayList()
        get() {
            if (createdOn < stageController.currentStage && initialDeclarations == null) {
                initialDeclarations = Collections.unmodifiableList(ArrayList(field))
            }

            return if (stageController.canAccessDeclarationsOf(this)) {
                ensureLowered()
                field
            } else {
                initialDeclarations ?: field
            }
        }

    override var typeParametersField: List<IrTypeParameter> = emptyList()

    override var typeParameters: List<IrTypeParameter>
        get() = getCarrier().typeParametersField
        set(v) {
            if (typeParameters !== v) {
                setCarrier().typeParametersField = v
            }
        }

    override var superTypesField: List<IrType> = emptyList()

    override var superTypes: List<IrType>
        get() = getCarrier().superTypesField
        set(v) {
            if (superTypes !== v) {
                setCarrier().superTypesField = v
            }
        }

    override var metadataField: MetadataSource? = null

    override var metadata: MetadataSource?
        get() = getCarrier().metadataField
        set(v) {
            if (metadata !== v) {
                setCarrier().metadataField = v
            }
        }

    override var modalityField: Modality = modality

    override var modality: Modality
        get() = getCarrier().modalityField
        set(v) {
            if (modality !== v) {
                setCarrier().modalityField = v
            }
        }

    override var attributeOwnerIdField: IrAttributeContainer = this

    override var attributeOwnerId: IrAttributeContainer
        get() = getCarrier().attributeOwnerIdField
        set(v) {
            if (attributeOwnerId !== v) {
                setCarrier().attributeOwnerIdField = v
            }
        }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitClass(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        thisReceiver?.accept(visitor, data)
        typeParameters.forEach { it.accept(visitor, data) }
        declarations.forEach { it.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        thisReceiver = thisReceiver?.transform(transformer, data)
        typeParameters = typeParameters.mapOptimized { it.transform(transformer, data) }
        declarations.transform { it.transform(transformer, data) }
    }
}
