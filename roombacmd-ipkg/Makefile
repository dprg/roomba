include $(TOPDIR)/rules.mk

PKG_NAME:=roombacmd
PKG_VERSION:=1.1
PKG_RELEASE:=1
PKG_MD5SUM:=0af97b64991d605f7877d2d7e09b3ea7

PKG_SOURCE_URL:=http://roombahacking.com/software/roombacmd
PKG_SOURCE:=$(PKG_NAME)-$(PKG_VERSION).tar.gz
PKG_CAT:=zcat

PKG_BUILD_DIR:=$(BUILD_DIR)/$(PKG_NAME)
PKG_INSTALL_DIR:=$(PKG_BUILD_DIR)/ipkg-install

include $(TOPDIR)/package/rules.mk

$(eval $(call PKG_template,ROOMBACMD,$(PKG_NAME),$(PKG_VERSION)-$(PKG_RELEASE),$(ARCH)))

$(PKG_BUILD_DIR)/.configured: $(PKG_BUILD_DIR)/.prepared
	#Since there is no configure script, we can directly go to the building step
	touch $@

$(PKG_BUILD_DIR)/.built:
	rm -rf $(PKG_INSTALL_DIR)
	mkdir -p $(PKG_INSTALL_DIR)/usr/bin
	#Note here that we pass cross-compiler as default compiler to use
	$(MAKE) -C $(PKG_BUILD_DIR) \
		CC=$(TARGET_CC) \
		$(TARGET_CONFIGURE_OPTS) \
		prefix="$(PKG_INSTALL_DIR)/usr"
	$(CP) $(PKG_BUILD_DIR)/roombacmd $(PKG_INSTALL_DIR)/usr/bin
	touch $@

$(IPKG_ROOMBACMD):
	install -d -m0755 $(IDIR_ROOMBACMD)/usr/bin
	$(CP) $(PKG_INSTALL_DIR)/usr/bin/roombacmd $(IDIR_ROOMBACMD)/usr/bin
	$(RSTRIP) $(IDIR_ROOMBACMD)
	$(IPKG_BUILD) $(IDIR_ROOMBACMD) $(PACKAGE_DIR)

mostlyclean:
	make -C $(PKG_BUILD_DIR) clean
	rm $(PKG_BUILD_DIR)/.built
