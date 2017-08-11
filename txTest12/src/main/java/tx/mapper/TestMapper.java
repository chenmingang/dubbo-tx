package tx.mapper;

import org.apache.ibatis.annotations.Param;

public interface TestMapper{

    int insert(@Param("i") int i);
}