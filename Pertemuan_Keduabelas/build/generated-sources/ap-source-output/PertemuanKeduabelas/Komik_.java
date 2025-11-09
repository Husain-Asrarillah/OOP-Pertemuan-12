package PertemuanKeduabelas;

import PertemuanKeduabelas.Pengarang;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-11-09T09:59:18", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Komik.class)
public class Komik_ { 

    public static volatile SingularAttribute<Komik, Pengarang> idPengarang;
    public static volatile SingularAttribute<Komik, Integer> tahunTerbit;
    public static volatile SingularAttribute<Komik, Integer> idKomik;
    public static volatile SingularAttribute<Komik, String> genre;
    public static volatile SingularAttribute<Komik, String> judul;

}