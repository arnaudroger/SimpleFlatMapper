package org.simpleflatmapper.test.map;

import java.util.Map;
import java.util.Set;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterBiFunction;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.getter.BiFunctionGetter;
import org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest.A;
import org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest.A.Builder;
import org.simpleflatmapper.util.BiFunction;


public final class AsmBiInstantiatorBuilderFromObjects_AndMappingContextIntobsAndid_I4 implements BiInstantiator<Object[], MappingContext, A> {
    final Instantiator<Void, Builder> builderInstantiator;
    final Getter factory_id;
    final ContextualGetterBiFunction factory_bs;

    public AsmBiInstantiatorBuilderFromObjects_AndMappingContextIntobsAndid_I4(Map<java.lang.String, BiFunction<Object[], MappingContext, ?>> var1, Instantiator<Void, Builder> var2) {
        this.builderInstantiator = var2;
        this.factory_id = (Getter)((BiFunctionGetter)var1.get("id")).getGetter();
        this.factory_bs = (ContextualGetterBiFunction)var1.get("bs");
    }

    public A newInstance(Object[] var1, MappingContext var2) throws Exception {
        Builder var4 = (Builder)this.builderInstantiator.newInstance(null);
        Integer var3 = (Integer)this.factory_id.get(var1);
        if (var3 != null) {
            int var5 = var3;
            var4.setId(var5);
        }

        Set var6 = (Set)this.factory_bs.apply(var1, var2);
        if (var6 != null) {
            var4.setBs(var6);
        }

        return var4.build();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        return var1.append(this.getClass().getSimpleName()).append("{").append("parameter0=").append("Parameter{name='id', type=int, resolvedType=int}").append(", parameter0=").append("org.simpleflatmapper.reflect.Getter<java.lang.Object[], P>").append(", parameter1=").append("Parameter{name='bs', type=interface java.util.Set, resolvedType=java.util.Set<org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest$B>}").append(", parameter1=").append("class org.simpleflatmapper.map.mapper.MapperBiFunctionAdapter").append("}").toString();
    }
}