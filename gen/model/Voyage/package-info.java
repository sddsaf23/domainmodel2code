//�����޽������ĵİ�ע��
@BoundedContext(name = "Voyage")
	 
//�����ɵ������޽�������ӳ���ϵ
@DownStreamContext(upStreamContextName = "Location", downStreamContextType = DownStreamContextType.Default) 
	 
//�����ɵ������޽�������ӳ���ϵ
@UpStreamContext(downStreamContextName = "Cargo", upStreamContextType = UpStreamContextType.Default) 


package gen.model.Voyage;

import gen.annotation.BoundedContext;
import gen.annotation.Partnership;
import gen.annotation.DownStreamContext;
import gen.annotation.UpStreamContext;