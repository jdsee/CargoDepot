package htw.prog3.persistence.jbp;

import htw.prog3.sm.core.*;

import java.beans.*;
import java.math.BigDecimal;
import java.time.Duration;

public class XmlEncodingHelper {

    public static XmlEncodingHelper create() {
        return new XmlEncodingHelper();
    }

    /**
     * I could not find the solution on my own, so I copied this one.
     * <p>
     * This solution is copied from:
     * https://gist.github.com/sachin-handiekar/185f5de2a9e027783a9f
     *
     * @param encoder The encoder of which the persistence delegate is set.
     */
    public void setBigDecimalPersistenceDelegate(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(BigDecimal.class, new DefaultPersistenceDelegate() {
            @Override
            protected Expression instantiate(Object oldInstance, Encoder out) {
                BigDecimal bd = (BigDecimal) oldInstance;
                return new Expression(oldInstance, oldInstance.getClass(), "new", new Object[]{
                        bd.toString()
                });
            }

            @Override
            protected boolean mutatesTo(Object oldInstance, Object newInstance) {
                return oldInstance.equals(newInstance);
            }
        });
    }

    public void setDurationPersistenceDelegate(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(Duration.class, new DefaultPersistenceDelegate() {
            @Override
            protected Expression instantiate(Object oldInstance, Encoder out) {
                Duration duration = (Duration) oldInstance;
                return new Expression(oldInstance, oldInstance.getClass(), "ofDays",
                        new Object[]{duration.toDays()}
                );
            }
        });
    }

    public void setCustomerPersistenceDelegate(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(CustomerImpl.class,
                new DefaultPersistenceDelegate(new String[]{"name", "maxValue", "maxDurationOfStorage"}));

    }

    public void setUnitisedCargoPersistenceDelegate(XMLEncoder encoder)
            throws IntrospectionException, NoSuchMethodException {
        encoder.setPersistenceDelegate(UnitisedCargoImpl.class,
                new DefaultPersistenceDelegate(new String[]{
                        "owner", "value", "durationOfStorage", "hazards", "fragile", "inspectionDate"
                })
        );
        BeanInfo info = Introspector.getBeanInfo(UnitisedCargoImpl.class);
        setHazardsReadMethod(info);
    }

    private void setHazardsReadMethod(BeanInfo info) throws NoSuchMethodException, IntrospectionException {
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (pd.getName().equals("hazards")) {
                pd.setReadMethod(UnitisedCargoImpl.class.getMethod("getSimpleHazards"));
            }
        }
    }

    public void setLiquidBulkCargoPersistenceDelegate(XMLEncoder encoder)
            throws IntrospectionException, NoSuchMethodException {
        encoder.setPersistenceDelegate(LiquidBulkCargoImpl.class,
                new DefaultPersistenceDelegate(new String[]{
                        "owner", "value", "durationOfStorage", "hazards", "pressurized", "inspectionDate"
                })
        );
        BeanInfo info = Introspector.getBeanInfo(LiquidBulkCargoImpl.class);
        setHazardsReadMethod(info);
    }

    public void setMixedCargoPersistenceDelegate(XMLEncoder encoder)
            throws IntrospectionException, NoSuchMethodException {
        encoder.setPersistenceDelegate(MixedCargoLiquidBulkAndUnitisedImpl.class,
                new DefaultPersistenceDelegate(new String[]{
                        "owner", "value", "durationOfStorage", "hazards", "pressurized", "fragile", "inspectionDate"
                })
        );
        BeanInfo info = Introspector.getBeanInfo(MixedCargoLiquidBulkAndUnitisedImpl.class);
        setHazardsReadMethod(info);
    }

    public void setStorageItemPersistenceDelegate(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(StorageItem.class,
                new DefaultPersistenceDelegate(new String[]{"cargo", "storagePosition", "storageDate"}));
    }

    public void setCustomerRecordPersistenceDelegate(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(CustomerRecord.class,
                new DefaultPersistenceDelegate(new String[]{"customer", "storageItems", "totalValue", "totalDurationOfStorage"}));
    }
}
