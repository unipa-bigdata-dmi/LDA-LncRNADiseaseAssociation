# LDA-LncRNADiseaseAssociation
# Datasets
The following section describe the datasets used for the classification.

|  Dataset    | miRNA | lncRNA | diseases | GS combinations | All combinations |
|--------|-------|--------|----------|-----------------|------------------|
| HMDD v2 | 524   | 1114   | 380      | 2625            | 423320           |
| HMDD v3.2 | 1065  | 1114   | 885      | 2520            | 985890           |

## miRNA-disease
Nel calcolo dei valori seguenti, sono state utilizzate le supplementary table fornite dagli autori [qui](https://www.nature.com/articles/srep13186#Sec14) e i dataset originali (sono forniti i link per scaricarli). Ad ogni valore sono stati rimossi eventuali spazi a inizio e fine parola, ed è stato normalizzato rendendolo tutto maiuscolo.
### [HMDD v2 (Jan 2015)](https://static-content.springer.com/esm/art%3A10.1038%2Fsrep13186/MediaObjects/41598_2015_BFsrep13186_MOESM4_ESM.xls)
- miRNA: **495**
- diseases: **380**
- associazioni: **5425**

Gli autori menzionano di aver identificato 383 malattie distinte, mentre nel nostro lavoro ne sono state individuate solo 380. Le tre malattie mancanti sono state erroneamente aggiunte dagli autori dell'altro articolo, in quanto risultavano distinte per uno spazio a fine nome. Le malattie in questione sono: *Stomach Neoplasms*, *Osteosarcoma* e *Glioblastoma*.
### [HMDD v3.2](http://www.cuilab.cn/static/hmdd3/data/alldata.txt)
- miRNA: **1206**
- disease: **894**
- associazioni: **18732**

Nell'aggiornamento del dataset HMDD dalla versione 2 alla 3.2, i propretari del dataset hanno apportato modifiche ai nomi delle disease. Il mapping è reperibile al seguente [link](http://www.cuilab.cn/static/hmdd3/data/disease_mapping2019.txt). Al fine di rimanere coerenti nei dati, è stato applicato il mapping dalla versione 3.2 alla versione 2. Solo tre malattie non sono state mappate:
- LEUKEMIA, LYMPHOCYTIC, CHRONIC -> LEUKEMIA, LYMPHOBLASTIC, CHRONIC, B-CELL: le due versioni hanno associati gli stessi 2 lncRNA nel dataset lncRNA-disease 
- NEOPLASMS -> CARCINOMA: nel dataset LncRNA-Disease sono presenti 11 associazioni tra lncRNA e "neoplasms", mentre 1 sola con "carcinoma" 
- TROPHOBLASTS -> ?: non vi è mapping

Successivamente al mapping è stata applicata una pulizia dei miRNA, allo scopo di mantenere solamente la forma *mature*. Ciò si è tradotto nell'identificare i miRNA dalla loro base comune. Ad esempio:
```
HSA-MIR-125B-1 |
               |  => HSA-MIR-125B
HSA-MIR-125B-2 |
```
A seguito di questo processo di pulizia, sono stati individuati:
- miRNA: **1058**
- disease: **885**
- associazioni: **16904**

## miRNA - lncRNA
### [starBase 2.0](https://static-content.springer.com/esm/art%3A10.1038%2Fsrep13186/MediaObjects/41598_2015_BFsrep13186_MOESM5_ESM.xls)
- miRNA: **275**
- lncRNA: **1114**
- associazioni: **10112**

Gli autori menzionano di aver identificato "*...circa 132 miRNAs...*", mentre nel nostro lavoro ne sono stati individuati quasi il doppio, ovvero *275*. In questo caso non è possibile stabilire il motivo di tale differenza, dunque il lavoro procederà con l'uso dei 275 miRNA.
## lncRNA - disease
### [LncRNADisease](https://static-content.springer.com/esm/art%3A10.1038%2Fsrep13186/MediaObjects/41598_2015_BFsrep13186_MOESM6_ESM.xls)
- lncRNA: **35**
- diseases: **75**
- associazioni: **183**

Se si lavora con il dataset HMDD v3.2 verranno rimosse 4 associazioni associate alle tre malattie di cui non si è potuto applicare il mapping:
- LEUKEMIA, LYMPHOBLASTIC, CHRONIC <- LEUKEMIA, LYMPHOBLASTIC, CHRONIC, CELL-B
- CARCINOMA <- NEOPLASMS
- TROPHOBLASTS <- NA

ricavando:
- lncRNA: **35**
- diseases: **72**
- associazioni: **179**
___
# Models
# Modello HGLDA
Il modello *HGLDA* proposto dagli autori si basa sul calcolo della distribuzione ipergeometrica alle coppie lncRNA-disease, tenendo conto dei miRNA che hanno in comune. La validità di un'associazione lncRNA-disease viene misurata con il P-value, definito come:
<!-- $$
    P = 1 - \sum_{i=0}^{x-1}\frac{{L\choose i}{N-L\choose M-i}}{{N\choose M}}
$$ --> 

<div align="center"><img style="background: white;" src="svg/IPcRAbI88E.svg"></div>

in cui `N` è il numero totale di miRNA associati ai lncRNA o alle diseases, `M` è il numero di miRNA che interagiscono con il lncRNA dato, `L` è il numero di miRNA che interagiscono con la disease data, infine `x` è il numero di miRNA che interagiscono con entrambi il lncRNA e la disease.

Una volta calcolato il P-value, viene applicata la correzione FDR (0.05) in modo da identificare le potenziali associazioni lncRNA-disease.

# Modello Centralità
Tre varianti di funzione score sono state implementate, in cui
<!-- $$M_{LD}[i,j]$$ --> 
<div align="center"><img style="background: white;" src="svg/qmPAha9b2j.svg"></div> rappresenta il numero di miRNA in comune tra il lncRNA `i` e la malattia `j`
<!-- $$M_{LL}[i,j]$$ --> 
<div align="center"><img style="background: white;" src="svg/u7hsphbQpf.svg"></div> rappresenta il numero di miRNA in comune tra il lncRNA `i` e il lncRNA `j`
<!-- $$n=min(M_{LL}[i,i], n_j)$$ --> 
<div align="center"><img style="background: white;" src="svg/0LMBI59aFG.svg"></div>
<!-- $$n_j$$ --> 
<div align="center"><img style="background: white;" src="svg/0bh1GCR1jo.svg"></div> è il numero di miRNA associati alla malattia `j`
<!-- $$ \text{Funzione 1: }S(l_i,d_j)=\alpha\Big(\frac{M_{LD}[i,j]}{n}\Big)+(1-\alpha)\Big(\frac{\sum_x M_{LL}[i,x] * M_{LD}[x,j]}{\sum_x M_{LL}[x,x]*n_j}\Big)$$ --> 

<div align="center"><img style="background: white;" src="svg/iHvUM90UgN.svg"></div>
___

# Build
There are two ways to build this project:
- manually build using Maven
- automated build using docker-compose

### Maven Build
The Maven build will create the jar file into `target/` folder.
```
mvn clean install
```
### Docker Build
The Docker build will create the jar file into `target/` folder.
```
docker-compose up
```
The docker script requires to setup the maven local repository, in order to avoid the download of the packages for each build. To this end, modify the line
```
    volumes:
    - ...
    - your_local_directory:/root/.m2 # TODO: change "your_local_directory" with the path to the local .m2 package repository
    command: ...
```
Generally, the .m2 folder is placed into `C://Users/<You>/.m2` or `/Users/<You>/.m2`.
## Execution
The execution of the jar via terminal is done with this command:
```
java -jar ./target/lda.jar <arguments>
```
There are several arguments that can be used. For more information use the argument `-h`. Using the `-h` argument will print the helper, ignoring the other arguments. If no arguments are specified, the helper will be shown automatically.

#TODO
- aggiungere descrizione degli argomenti
- aggiungere descrizione dei modelli preimpostati
- aggiungere descrizione dei risultati