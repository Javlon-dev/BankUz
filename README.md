# Тестовое задание

- Описание: P2P переводы
- Средства разработки: Java (jdk 1.8)
- Framework: springBootVersion = '2.0.0.RELEASE'

- Cистема автоматической сборки: Gradle
- База данных: PostgreSQL

1. Авторизация

- Создать JWT авторизацию (Spring security)

2. Подготовить 2 проекта (2 приложение банка)

# Узбекский банк

Сервисы Карт:

- Uzcard
- Humo
- Visa
- У всех должно быть Таблица с этими данными `Card`

```
   private String id;
   private String username;
   private String pan;
   private String expiry;
   private Integer status;
   private String phone;
   private String fullName;
   protected String balance;
```

# Российский банк

Сервисы Карт:

- МИР
- Visa
- У всех должно быть Таблица с этими данными `Card`

```
    private String id;
    private String username;
    private String pan;
    private String expiry;
    private Integer status;
    private String phone;
    private String fullName;
    protected String balance;
```

- Общая таблица для хранений карт `Card`

```
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Basic
    @Column(name = "create_time", insertable = false, updatable = false)
    @Type(type = "timestamp")
    private Date createTime;
    
    @Column(name = "provider")
    private String provider;
    
    @Column(name = "provider_card_id")
    private String providerCardId;
    
    @Column(name = "masked_pan")
    private String maskedPan;
    
    @Column(name = "bank_client_id")
    private String bankClientId;
    
    // например avsls, meest или mc
    @Column(name = "card_type")
    private String cardType;
    
    //открытый PAN карты. Для uzcard
    @Column(name = "pan")
    private String pan;
    
    @Column(name = "bank_id", length = 6)
    private String bankId;
    
    //Храним только для uzcard. Формат YYMM
    @Column(name = "expiry")
    private String expiry;
    
    @NotAudited
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id", referencedColumnName = "mfo_code",
    nullable = false, updatable = false, insertable = false)
    private Bank bank;

```
