//�����޽������ĵİ�ע��
@BoundedContext(name = "Location")
	 
//�����ɵ������޽�������ӳ���ϵ
@UpStreamContext(downStreamContextName = "LocationShare", upStreamContextType = UpStreamContextType.Default) 
	 
//�����ɵ������޽�������ӳ���ϵ
@UpStreamContext(downStreamContextName = "Voyage", upStreamContextType = UpStreamContextType.Default) 


package gen.model.Location;

import gen.annotation.BoundedContext;
import gen.annotation.Partnership;
import gen.annotation.DownStreamContext;
import gen.annotation.UpStreamContext;