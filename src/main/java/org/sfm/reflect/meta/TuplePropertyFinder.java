package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.TypeHelper;

import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TuplePropertyFinder<T> extends AbstractIndexPropertyFinder<T> {

    private final List<InstantiatorDefinition> instantiatorDefinitions;

    public TuplePropertyFinder(TupleClassMeta<T> tupleClassMeta) {
        super(tupleClassMeta);
        this.instantiatorDefinitions = tupleClassMeta.getInstantiatorDefinitions();

        for(int i = 0; i < tupleClassMeta.getTupleSize(); i++) {
			elements.add(newIndexedElement(tupleClassMeta, i));
		}
	}

	private <E> IndexedElement<T, E> newIndexedElement(TupleClassMeta<T> tupleClassMeta, int i) {
		ConstructorPropertyMeta<T, E> prop =
                newConstructorPropertyMeta(tupleClassMeta, i);
		ClassMeta<E> classMeta = tupleClassMeta.getReflectionService().getClassMeta(prop.getType());
		return new IndexedElement<T, E>(prop, classMeta);
	}

    private <E> ConstructorPropertyMeta<T, E> newConstructorPropertyMeta(TupleClassMeta<T> tupleClassMeta, int i) {
        Class<T> tClass = TypeHelper.toClass(tupleClassMeta.getType());

        final Parameter parameter = instantiatorDefinitions.get(0).getParameters()[i];

        return new ConstructorPropertyMeta<T, E>(parameter.getName(), tupleClassMeta.getReflectionService(),
                parameter, tClass);
    }

    @Override
    protected boolean isValidIndex(IndexedColumn indexedColumn) {
        return indexedColumn.getIndexValue() < elements.size();
    }
    @Override
    protected IndexedElement<T, ?> getIndexedElement(IndexedColumn indexedColumn) {
        return elements.get(indexedColumn.getIndexValue());
    }

    protected IndexedColumn extrapolateIndex(PropertyNameMatcher propertyNameMatcher) {
        for(int i = 0; i < elements.size(); i++) {
            IndexedElement element = elements.get(i);

            if (element.getElementClassMeta() != null) {
                PropertyFinder<?> pf = element.getPropertyFinder();
                PropertyMeta<?, Object> property = pf.findProperty(propertyNameMatcher);
                if (property != null) {
                    if (!element.hasProperty(property)) {
                        return new IndexedColumn(i , propertyNameMatcher);
                    }
                }

            }
        }
        return null;
    }

    @Override
	public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
		return instantiatorDefinitions;
	}

    @Override
    public <E> ConstructorPropertyMeta<T, E> findConstructor(InstantiatorDefinition instantiatorDefinition) {
        return null;
    }

}
