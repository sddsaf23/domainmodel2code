//���ɹ����ں�ģ��İ�ע��
@SharedKernel(name = "LocationShare", oneContextName = "Location", theOtherContextName = "Cargo")

	 
//�����ɵ������޽�������ӳ���ϵ
@DownStreamContext(upStreamContextName = "Location", downStreamContextType = DownStreamContextType.Default) 

package gen.model.LocationShare;

import gen.annotation.SharedKernel;
import gen.annotation.Partnership;
import gen.annotation.DownStreamContext;
import gen.annotation.UpStreamContext;