package com.printtex.printtex_pos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int id;
    @Column(name = "email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String email;
    @Column(name = "password")
    @Length(min = 8, message = "*Your password must have at least 8 characters")
    @NotEmpty(message = "*Please provide your password")
    @Transient
    @JsonIgnore
    private String password;
    @Column(name = "name")
    @NotEmpty(message = "*Please provide your name")
    private String name;
    @Column(name = "active")
    private int active;
    @Transient
//	@NotEmpty(message = "*Please provide role")
    private String role;
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Branch branch;

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsNeedPermission() {
        return isNeedPermission;
    }

    public void setIsNeedPermission(Boolean isNeedPermission) {
        this.isNeedPermission = isNeedPermission;
    }

    @Column(name = "is_need_permission")
    private Boolean isNeedPermission;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
	/*@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "user_tiles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tiles_id"))
	private List<Tiles> tiles;*/

    public User(String email, String password, String name, int active, Set<Role> roles, List<Tiles> tiles) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.active = active;
        this.roles = roles;
//		this.tiles = tiles;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

//	public List<Tiles> getTiles() {
//		return tiles;
//	}

//	public void setTiles(List<Tiles> tiles) {
//		this.tiles = tiles;
//	}

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
