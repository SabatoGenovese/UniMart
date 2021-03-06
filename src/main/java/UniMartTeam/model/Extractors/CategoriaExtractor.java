package UniMartTeam.model.Extractors;

import UniMartTeam.model.Beans.Categoria;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaExtractor {

    public static Categoria Extract(ResultSet rs, String alias) throws SQLException {

        if(rs != null) {
            Categoria c = new Categoria();

            if(!alias.isEmpty())
                alias+=".";

            c.setNome(rs.getString(alias+"nome"));
            c.setAliquota(rs.getFloat(alias+"aliquota"));
            return c;
        }
        return null;
    }
}
