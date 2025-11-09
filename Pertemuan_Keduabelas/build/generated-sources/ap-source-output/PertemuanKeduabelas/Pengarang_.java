package PertemuanKeduabelas;

import PertemuanKeduabelas.Komik;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-11-09T09:59:18", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Pengarang.class)
public class Pengarang_ { 

    public static volatile SingularAttribute<Pengarang, String> namaPengarang;
    public static volatile SingularAttribute<Pengarang, Integer> idPengarang;
    public static volatile SingularAttribute<Pengarang, String> asalNegara;
    public static volatile CollectionAttribute<Pengarang, Komik> komikCollection;
    public static volatile SingularAttribute<Pengarang, String> email;

}