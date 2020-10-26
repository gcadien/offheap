At Runtime add --add-exports java.base/jdk.internal.ref=ALL-UNNAMED 
to the program arguments , else the resize operation will fail.


The offheap map (and set) is useful when a large amount of data is to be read , but a small subset of the data will be required at any time. 
The map stores a small amount of data in heap memory and the rest is stored offheap in a memory mapped file. 
Data is evicted from the heap via LRU and updated from the memory map as required. 
For simplicity of layout, a max size has to be specified for the key and value. 

The other collections are likely not that useful due to the overhead of ser/deser when moving into heap from the memory map. 
