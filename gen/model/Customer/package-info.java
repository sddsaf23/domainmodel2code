//�����޽������ĵİ�ע��
@BoundedContext(name = "Customer")
	 
//�����ɵ������޽�������ӳ���ϵ
@UpStreamContext(downStreamContextName = "Cargo", upStreamContextType = UpStreamContextType.Default) 
	 
//�����ɵ������޽�������ӳ���ϵ
@UpStreamContext(downStreamContextName = "CustomerShare", upStreamContextType = UpStreamContextType.Default) 


package gen.model.Customer;

import gen.annotation.BoundedContext;
import gen.annotation.Partnership;
import gen.annotation.DownStreamContext;
import gen.annotation.UpStreamContext;